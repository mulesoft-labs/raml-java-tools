package org.raml.pojotoraml;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.RamlTypeFactory;
import org.raml.pojotoraml.types.ScalarType;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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

        RamlType type = RamlTypeFactory.forType(clazz, classParserFactory.createParser(clazz), adjusterFactory).or(new RamlTypeSupplier(clazz));

        if ( type.isScalar()) {

            return new Result(null, Collections.<String, TypeDeclarationBuilder>emptyMap());
        }

        Map<String, TypeDeclarationBuilder> dependentTypes = new HashMap<>();
        TypeDeclarationBuilder builder = handleSingleType(clazz, dependentTypes);
        dependentTypes.remove(builder.id());
        return new Result(builder, dependentTypes);
    }

    @Override
    public TypeBuilder name(Class<?> clazz) {

        RamlAdjuster adjuster = this.adjusterFactory.createAdjuster(clazz);

        ClassParser parser = classParserFactory.createParser(clazz);
        RamlType type = RamlTypeFactory.forType(clazz, parser, adjusterFactory).or(new RamlTypeSupplier(clazz));

        if ( type.isScalar()) {

            return type.getRamlSyntax();
        }

        final String simpleName = adjuster.adjustTypeName(clazz, clazz.getSimpleName());
        return TypeBuilder.type(simpleName);
    }

    @Override
    public TypeBuilder name(Type type) {

        if ( type instanceof Class) {
            return name((Class<?>)type);
        } else {
            if ( type instanceof ParameterizedType ) {

                ParameterizedType pt = (ParameterizedType) type;
                if ( pt.getRawType() instanceof Class && Collection.class.isAssignableFrom((Class)pt.getRawType()) &&  pt.getActualTypeArguments().length == 1) {

                    Class<?> cls = (Class<?>) pt.getActualTypeArguments()[0];
                    TypeBuilder builder = name(cls);
                    return TypeBuilder.arrayOf(builder);
                } else {
                    throw new IllegalArgumentException("can't parse type " + pt);
                }
            } else {

                throw new IllegalArgumentException("can't parse type " + type);
            }
        }
    }

    private TypeDeclarationBuilder handleSingleType(Class<?> clazz, Map<String, TypeDeclarationBuilder> builtTypes) {

        ClassParser parser = classParserFactory.createParser(clazz);

        RamlType quickType = RamlTypeFactory.forType(clazz, parser, adjusterFactory).or(new RamlTypeSupplier(clazz));
        if ( quickType.isScalar()) {

            return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax().id()).ofType(quickType.getRamlSyntax());
        }

        if ( quickType.isEnum()) {

            return handleEnum(quickType, adjusterFactory.createAdjuster(clazz), builtTypes);
        }

        final String simpleName = adjusterFactory.createAdjuster(clazz).adjustTypeName(clazz, clazz.getSimpleName());

        TypeBuilder builder = buildSuperType(clazz, builtTypes);
        builder = adjusterFactory.createAdjuster(clazz).adjustType(clazz, simpleName, builder);

        TypeDeclarationBuilder typeDeclaration = TypeDeclarationBuilder.typeDeclaration(simpleName).ofType(builder);
        if ( !ScalarType.isRamalScalarType(simpleName)) {
            builtTypes.put(simpleName, typeDeclaration);
        }

        for (Property property : parser.properties(clazz)) {

            Optional<RamlType> ramlTypeOptional = RamlTypeFactory.forType(property.type(), parser, adjusterFactory);

            if ( ! ramlTypeOptional.isPresent() ) {

                RamlType type = resolveUnknownTypeInProperty(adjusterFactory, clazz, builder, typeDeclaration, property);
                if ( type != null) {

                    builder.withProperty(TypePropertyBuilder.property(property.name(), type.getRamlSyntax()));
                }

                continue;
            }

            RamlType ramlType = ramlTypeOptional.get();
            if ( ramlType.isScalar() ) {
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustScalarProperty(typeDeclaration, property,  typePropertyBuilder));
            } else {

                final String subSimpleName = adjusterFactory.createAdjuster(clazz).adjustTypeName(clazz, ramlType.type().getSimpleName());
                if ( ! builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(ramlType.type(), builtTypes);
                }
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());

                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustComposedProperty(typeDeclaration, property,  typePropertyBuilder));
            }
        }

        return typeDeclaration;
    }

    private TypeDeclarationBuilder handleEnum(final RamlType quickType, final RamlAdjuster adjuster, Map<String, TypeDeclarationBuilder> builtTypes) {

        Class<? extends Enum> c = (Class<? extends Enum>) quickType.type();
        TypeBuilder typeBuilder = TypeBuilder.type().enumValues(
                Arrays.stream(c.getEnumConstants()).map(new java.util.function.Function<Enum, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Enum o) {
                        return adjuster.adjustEnumValue(quickType.type(), o.name());
                    }
                }).collect(Collectors.toList()).toArray(new String[0]));


        adjuster.adjustType(quickType.type(), quickType.getRamlSyntax().id(), typeBuilder);
        TypeDeclarationBuilder typeDeclarationBuilder = TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax().id()).ofType(typeBuilder);

        builtTypes.put(quickType.getRamlSyntax().id(), typeDeclarationBuilder);
        return typeDeclarationBuilder;
    }

    private TypeBuilder buildSuperType(Class<?> clazz, Map<String, TypeDeclarationBuilder> builtTypes) {
        ClassParser parser = classParserFactory.createParser(clazz);
        Collection<Type> types = parser.parentClasses(clazz);
        ArrayList<String> typeNames = new ArrayList<>();
        if ( types != null ) {
            for (Type supertype : types) {

                // Currently a workaround.
                if ( supertype instanceof Class && ((Class)supertype).getPackage().getName().startsWith("java.")) {
                    continue;
                }

                RamlType ramlType = RamlTypeFactory.forType(supertype, parser, adjusterFactory).or(new RamlTypeSupplier(clazz));

                final String subSimpleName = adjusterFactory.createAdjuster(ramlType.type()).adjustTypeName(ramlType.type(), ramlType.type().getSimpleName());
                if (!builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(ramlType.type(), builtTypes);
                }

                typeNames.add(subSimpleName);
            }
        }

        TypeBuilder builder;
        if ( typeNames.isEmpty()) {
            builder = TypeBuilder.type("object");
        } else {
            builder = TypeBuilder.type(typeNames.toArray(new String[0]));
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
            final TypeBuilder tb = adjusterFactory.createAdjuster(clazz).adjustForUnknownType(clazz);
            return new GeneratedRamlType(clazz, tb);
        }

    }

    private static RamlType resolveUnknownTypeInProperty(AdjusterFactory adjusterFactory, Class<?> clazz, TypeBuilder typeBuilder, TypeDeclarationBuilder typeDeclarationBuilder, Property property) {

        final TypeBuilder tb = adjusterFactory.createAdjuster(clazz).adjustForUnknownType(property.type());
        if ( tb != null ) {

            return new GeneratedRamlType(clazz, typeBuilder);
        } else {

            adjusterFactory.createAdjuster(clazz).adjustForUnknownTypeInProperty(clazz, typeBuilder, typeDeclarationBuilder,  property);
            return null;
        }
    }

}
