package org.raml.pojotoraml;

import com.google.common.base.Supplier;
import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.NodeShapeBuilder;
import org.raml.builder.PropertyShapeBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.RamlTypeFactory;
import org.raml.pojotoraml.types.ScalarType;
import org.raml.pojotoraml.util.TypeConversionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created. There, you have it.
 */
public class PojoToRamlImpl implements PojoToRaml {

    private final ClassParserFactory classParserFactory;
    private final AdjusterFactory adjusterFactory;

    private final SeenTypes dependentTypes = new SeenTypes();

    public PojoToRamlImpl(ClassParserFactory parser, AdjusterFactory adjusterFactory) {
        this.classParserFactory = parser;
        this.adjusterFactory = adjusterFactory;
    }

    @Override
    public Result classToRaml(final Type clazz) {

        RamlType type = RamlTypeFactory.forType(clazz, classParserFactory.createParser(clazz), adjusterFactory).orElseGet(new RamlTypeSupplier(clazz));

        if ( type.isScalar()) {

            return new Result(null, Collections.emptyMap());
        }

        DeclaredShapeBuilder<?> builder = handleSingleType(clazz, dependentTypes);
        dependentTypes.remove(builder);
        return new Result(builder, dependentTypes.namedAsMap());
    }

    private TypeShapeBuilder name(Class<?> clazz) {

        RamlAdjuster adjuster = this.adjusterFactory.createAdjuster(clazz);

        ClassParser parser = classParserFactory.createParser(clazz);
        RamlType type = RamlTypeFactory.forType(clazz, parser, adjusterFactory).orElseGet(new RamlTypeSupplier(clazz));

        if ( type.isScalar()) {

            return type.getRamlSyntax(this::typeShapeBuilder).asTypeShapeBuilder();
        }

        final String simpleName = adjuster.adjustTypeName(clazz, clazz.getSimpleName());
        DeclaredShapeBuilder<?> ramlSyntax = type.getRamlSyntax(this::typeShapeBuilder);
        dependentTypes.storeType(type.type(), ramlSyntax);
        return DeclaredShapeBuilder.typeDeclaration(simpleName).ofType(ramlSyntax.asTypeShapeBuilder()).asTypeShapeBuilder();
    }

    @Override
    public TypeShapeBuilder<?, ?> typeShapeBuilder(Type type) {


        if ( dependentTypes.hasType(type)) {
            return dependentTypes.byType(type).asTypeShapeBuilder();
        }

        return handleSingleType(type, dependentTypes).asTypeShapeBuilder();
    }

    private DeclaredShapeBuilder handleSingleType(Type clazz, SeenTypes builtTypes) {

        if ( builtTypes.hasType(clazz)) {
            return builtTypes.byType(clazz);
        }

        ClassParser parser = classParserFactory.createParser(clazz);

        RamlType quickType = RamlTypeFactory.forType(clazz, parser, adjusterFactory).orElseGet(new RamlTypeSupplier(clazz));
        if ( quickType.isScalar()) {

            return DeclaredShapeBuilder.anonymousType().ofType(quickType.getRamlSyntax(this::typeShapeBuilder).asTypeShapeBuilder());
        }

        if ( quickType.isEnum()) {

            return handleEnum(quickType, adjusterFactory.createAdjuster(clazz), builtTypes);
        }

        if ( quickType.isCollection()) {
            return quickType.getRamlSyntax(this::typeShapeBuilder);
        }

        final String simpleName = adjusterFactory.createAdjuster(clazz).adjustTypeName(clazz, TypeConversionUtils.typeToSimpleName(clazz));

        NodeShapeBuilder builder = buildSuperType(clazz, builtTypes);
        builder = adjusterFactory.createAdjuster(clazz).adjustType(clazz, simpleName, builder);

        DeclaredShapeBuilder typeDeclaration = DeclaredShapeBuilder.typeDeclaration(simpleName).ofType(builder);
        if ( !ScalarType.isRamlScalarType(simpleName) && ! quickType.isCollection()) {
            builtTypes.storeType(clazz, typeDeclaration);
        }

        for (Property property : parser.properties(clazz)) {

            Optional<RamlType> ramlTypeOptional = RamlTypeFactory.forType(property.type(), parser, adjusterFactory);

            if ( ! ramlTypeOptional.isPresent() ) {

                RamlType type = resolveUnknownTypeInProperty(adjusterFactory, clazz, builder, typeDeclaration, property);
                if ( type != null) {

                    builder.withProperty(PropertyShapeBuilder.property(property.name(), type.getRamlSyntax(this::typeShapeBuilder).asTypeShapeBuilder()));
                }

                continue;
            }

            RamlType ramlType = ramlTypeOptional.get();
            if ( ramlType.isScalar() ) {
                PropertyShapeBuilder propertyShapeBuilder = PropertyShapeBuilder.property(property.name(), ramlType.getRamlSyntax(this::typeShapeBuilder).asTypeShapeBuilder());
                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustScalarProperty(typeDeclaration, property, propertyShapeBuilder));
            } else if (ramlType.isCollection() ) {
                PropertyShapeBuilder propertyShapeBuilder = PropertyShapeBuilder.property(property.name(), ramlType.getRamlSyntax(this::typeShapeBuilder).asTypeShapeBuilder());
                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustScalarProperty(typeDeclaration, property, propertyShapeBuilder));
            } else {

                final String subSimpleName = adjusterFactory.createAdjuster(clazz).adjustTypeName(clazz, TypeConversionUtils.typeToSimpleName(ramlType.type()));
                if ( ! builtTypes.hasName(subSimpleName)) {

                    handleSingleType(ramlType.type(), builtTypes);
                }
                PropertyShapeBuilder propertyShapeBuilder = PropertyShapeBuilder.property(property.name(), ramlType.getRamlSyntax(this::typeShapeBuilder).asTypeShapeBuilder());
                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustComposedProperty(typeDeclaration, property, propertyShapeBuilder));
            }
        }

        return typeDeclaration;
    }

    private DeclaredShapeBuilder handleEnum(final RamlType quickType, final RamlAdjuster adjuster, SeenTypes builtTypes) {

        DeclaredShapeBuilder declaredShapeBuilder = quickType.getRamlSyntax(this::typeShapeBuilder);

        builtTypes.storeType(quickType.type(), declaredShapeBuilder);
        return declaredShapeBuilder;
    }

    private NodeShapeBuilder buildSuperType(Type clazz, SeenTypes builtTypes) {
        ClassParser parser = classParserFactory.createParser(clazz);
        Collection<Type> types = parser.parentClasses(clazz);
        ArrayList<String> typeNames = new ArrayList<>();
        if ( types != null ) {
            for (Type supertype : types) {

                // Currently a workaround.
                if ( supertype instanceof Class && ((Class)supertype).getPackage().getName().startsWith("java.")) {
                    continue;
                }

                RamlType ramlType = RamlTypeFactory.forType(supertype, parser, adjusterFactory).orElseGet(new RamlTypeSupplier(clazz));

                final String subSimpleName = adjusterFactory.createAdjuster(ramlType.type()).adjustTypeName(ramlType.type(), TypeConversionUtils.typeToSimpleName(ramlType.type()));
                if (!builtTypes.hasName(subSimpleName)) {

                    handleSingleType(ramlType.type(), builtTypes);
                }

                typeNames.add(subSimpleName);
            }
        }

        NodeShapeBuilder builder;
        if ( typeNames.isEmpty()) {
            builder = TypeShapeBuilder.inheritingObjectFromShapes();
        } else {
            List<DeclaredShapeBuilder<?>> shapes = builtTypes.findNamed(typeNames);
            builder = TypeShapeBuilder.inheritingObjectFromShapes(shapes.stream().map(DeclaredShapeBuilder::asTypeShapeBuilder).toArray(TypeShapeBuilder[]::new));
        }
        return builder;
    }

    private class RamlTypeSupplier implements Supplier<RamlType> {
        private final Type clazz;

        public RamlTypeSupplier(Type clazz) {
            this.clazz = clazz;
        }

        @Override
        public RamlType get() {
            final TypeShapeBuilder tb = adjusterFactory.createAdjuster(clazz).adjustForUnknownType(clazz);
            return new GeneratedRamlType(clazz, tb);
        }

    }

    private static RamlType resolveUnknownTypeInProperty(AdjusterFactory adjusterFactory, Type clazz, TypeShapeBuilder typeBuilder, DeclaredShapeBuilder declaredShapeBuilder, Property property) {

        final TypeShapeBuilder tb = adjusterFactory.createAdjuster(clazz).adjustForUnknownType(property.type());
        if ( tb != null ) {

            return new GeneratedRamlType(clazz, typeBuilder);
        } else {

            adjusterFactory.createAdjuster(clazz).adjustForUnknownTypeInProperty(clazz, (NodeShapeBuilder)typeBuilder, declaredShapeBuilder,  property);
            return null;
        }
    }

}
