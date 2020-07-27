package org.raml.pojotoraml;

import com.google.common.base.Supplier;
import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.NodeShapeBuilder;
import org.raml.builder.PropertyShapeBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.RamlTypeFactory;
import org.raml.pojotoraml.types.ScalarType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created. There, you have it.
 */
public class PojoToRamlImpl implements PojoToRaml {

    private final ClassParserFactory classParserFactory;
    private final AdjusterFactory adjusterFactory;

    public PojoToRamlImpl(ClassParserFactory parser, AdjusterFactory adjusterFactory) {
        this.classParserFactory = parser;
        this.adjusterFactory = adjusterFactory;
    }

    @Override
    public Result classToRaml(final Class<?> clazz) {

        RamlType type = RamlTypeFactory.forType(clazz, classParserFactory.createParser(clazz), adjusterFactory).orElseGet(new RamlTypeSupplier(clazz));

        if ( type.isScalar()) {

            return new Result(null, Collections.emptyMap());
        }

        SeenTypes dependentTypes = new SeenTypes();
        DeclaredShapeBuilder<?> builder = handleSingleType(clazz, dependentTypes);
        dependentTypes.remove(builder);
        return new Result(builder, dependentTypes.namedAsMap());
    }

    @Override
    public TypeShapeBuilder name(Class<?> clazz) {

        RamlAdjuster adjuster = this.adjusterFactory.createAdjuster(clazz);

        ClassParser parser = classParserFactory.createParser(clazz);
        RamlType type = RamlTypeFactory.forType(clazz, parser, adjusterFactory).orElseGet(new RamlTypeSupplier(clazz));

        if ( type.isScalar()) {

            return type.getRamlSyntax(adjuster).asTypeShapeBuilder();
        }

        final String simpleName = adjuster.adjustTypeName(clazz, clazz.getSimpleName());
        //JP inheritance.
        return DeclaredShapeBuilder.typeDeclaration(simpleName).ofType(TypeShapeBuilder.inheritingObjectFromShapes()).asTypeShapeBuilder();
    }

    @Override
    public TypeShapeBuilder name(Type type) {

        if ( type instanceof Class) {
            return name((Class<?>)type);
        } else {
            if ( type instanceof ParameterizedType ) {

                ParameterizedType pt = (ParameterizedType) type;
                if ( pt.getRawType() instanceof Class && Collection.class.isAssignableFrom((Class)pt.getRawType()) &&  pt.getActualTypeArguments().length == 1) {

                    Class<?> cls = (Class<?>) pt.getActualTypeArguments()[0];
                    TypeShapeBuilder builder = name(cls);
                    return TypeShapeBuilder.arrayOf(builder);
                } else {
                    throw new IllegalArgumentException("can't parse type " + pt);
                }
            } else {

                throw new IllegalArgumentException("can't parse type " + type);
            }
        }
    }

    private DeclaredShapeBuilder handleSingleType(Class<?> clazz, SeenTypes builtTypes) {

        ClassParser parser = classParserFactory.createParser(clazz);

        RamlType quickType = RamlTypeFactory.forType(clazz, parser, adjusterFactory).orElseGet(new RamlTypeSupplier(clazz));
        if ( quickType.isScalar()) {

            return DeclaredShapeBuilder.typeDeclaration(quickType.getRamlSyntax(adjusterFactory.createAdjuster(clazz)).id()).ofType(quickType.getRamlSyntax(adjusterFactory.createAdjuster(clazz)).asTypeShapeBuilder());
        }

        if ( quickType.isEnum()) {

            return handleEnum(quickType, adjusterFactory.createAdjuster(clazz), builtTypes);
        }

        final String simpleName = adjusterFactory.createAdjuster(clazz).adjustTypeName(clazz, clazz.getSimpleName());

        NodeShapeBuilder builder = buildSuperType(clazz, builtTypes);
        builder = adjusterFactory.createAdjuster(clazz).adjustType(clazz, simpleName, builder);

        DeclaredShapeBuilder typeDeclaration = DeclaredShapeBuilder.typeDeclaration(simpleName).ofType(builder);
        if ( !ScalarType.isRamlScalarType(simpleName)) {
            builtTypes.storeType(typeDeclaration);
        }

        for (Property property : parser.properties(clazz)) {

            Optional<RamlType> ramlTypeOptional = RamlTypeFactory.forType(property.type(), parser, adjusterFactory);

            if ( ! ramlTypeOptional.isPresent() ) {

                RamlType type = resolveUnknownTypeInProperty(adjusterFactory, clazz, builder, typeDeclaration, property);
                if ( type != null) {

                    builder.withProperty(PropertyShapeBuilder.property(property.name(), type.getRamlSyntax(adjusterFactory.createAdjuster(type.type())).asTypeShapeBuilder()));
                }

                continue;
            }

            RamlType ramlType = ramlTypeOptional.get();
            if ( ramlType.isScalar() ) {
                PropertyShapeBuilder propertyShapeBuilder = PropertyShapeBuilder.property(property.name(), ramlType.getRamlSyntax(adjusterFactory.createAdjuster(clazz)).asTypeShapeBuilder());
                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustScalarProperty(typeDeclaration, property, propertyShapeBuilder));
            } else {

                final String subSimpleName = adjusterFactory.createAdjuster(clazz).adjustTypeName(clazz, ramlType.type().getSimpleName());
                if ( ! builtTypes.hasName(subSimpleName)) {

                    handleSingleType(ramlType.type(), builtTypes);
                }
                DeclaredShapeBuilder<?> t = builtTypes.byName(subSimpleName);
                PropertyShapeBuilder propertyShapeBuilder = PropertyShapeBuilder.property(property.name(), t.asTypeShapeBuilder());

                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustComposedProperty(typeDeclaration, property, propertyShapeBuilder));
            }
        }

        return typeDeclaration;
    }

    private DeclaredShapeBuilder handleEnum(final RamlType quickType, final RamlAdjuster adjuster, SeenTypes builtTypes) {

        quickType.getRamlSyntax(adjuster);
        DeclaredShapeBuilder declaredShapeBuilder = quickType.getRamlSyntax(adjusterFactory.createAdjuster(quickType.type()));

        builtTypes.storeType(declaredShapeBuilder);
        return declaredShapeBuilder;
    }

    private NodeShapeBuilder buildSuperType(Class<?> clazz, SeenTypes builtTypes) {
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

                final String subSimpleName = adjusterFactory.createAdjuster(ramlType.type()).adjustTypeName(ramlType.type(), ramlType.type().getSimpleName());
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
            builder = TypeShapeBuilder.inheritingObjectFromShapes(shapes.stream().map(x -> x.asTypeShapeBuilder()).toArray(TypeShapeBuilder[]::new));
        }
        return builder;
    }

    private class RamlTypeSupplier implements Supplier<RamlType> {
        private final Class<?> clazz;

        public RamlTypeSupplier(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public RamlType get() {
            final TypeShapeBuilder tb = adjusterFactory.createAdjuster(clazz).adjustForUnknownType(clazz);
            return new GeneratedRamlType(clazz, tb);
        }

    }

    private static RamlType resolveUnknownTypeInProperty(AdjusterFactory adjusterFactory, Class<?> clazz, TypeShapeBuilder typeBuilder, DeclaredShapeBuilder declaredShapeBuilder, Property property) {

        final TypeShapeBuilder tb = adjusterFactory.createAdjuster(clazz).adjustForUnknownType(property.type());
        if ( tb != null ) {

            return new GeneratedRamlType(clazz, typeBuilder);
        } else {

            adjusterFactory.createAdjuster(clazz).adjustForUnknownTypeInProperty(clazz, (NodeShapeBuilder)typeBuilder, declaredShapeBuilder,  property);
            return null;
        }
    }

}
