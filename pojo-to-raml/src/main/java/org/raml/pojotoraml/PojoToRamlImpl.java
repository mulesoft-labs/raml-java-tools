package org.raml.pojotoraml;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
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
    public Result classToRaml(Class<?> clazz) {

        RamlType type = RamlTypeFactory.forType(clazz, classParserFactory.createParser(clazz), adjusterFactory);

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
        RamlType type = RamlTypeFactory.forType(clazz, parser, adjusterFactory);

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
        RamlType quickType = exploreType(parser, clazz);
        if ( quickType.isScalar()) {

            return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax().id()).ofType(quickType.getRamlSyntax());
        }

        if ( quickType.isEnum()) {

            return handleEnum(quickType, adjusterFactory.createAdjuster(clazz));
        }

        final String simpleName = adjusterFactory.createAdjuster(clazz).adjustTypeName(clazz, clazz.getSimpleName());

        TypeBuilder builder = buildSuperType(clazz, builtTypes);

        builder = adjusterFactory.createAdjuster(clazz).adjustType(clazz, simpleName, builder);
        TypeDeclarationBuilder typeDeclaration = TypeDeclarationBuilder.typeDeclaration(simpleName).ofType(builder);
        if ( !ScalarType.isRamalScalarType(simpleName)) {
            builtTypes.put(simpleName, typeDeclaration);
        }

        for (Property property : parser.properties(clazz)) {

            RamlType ramlType = exploreType(parser, property.type());

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

    private TypeDeclarationBuilder handleEnum(final RamlType quickType, final RamlAdjuster adjuster) {

        Class<? extends Enum> c = (Class<? extends Enum>) quickType.type();
        TypeBuilder typeBuilder = TypeBuilder.type().enumValues(
                FluentIterable.of(c.getEnumConstants()).transform(new Function<Enum, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Enum o) {
                        return adjuster.adjustEnumValue(quickType.type(), o.name());
                    }
                }).toArray(String.class)
        );

        adjuster.adjustType(quickType.type(), quickType.getRamlSyntax().id(), typeBuilder);
        return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax().id()).ofType(typeBuilder);
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

                RamlType ramlType = exploreType(parser, supertype);

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

    private RamlType exploreType(ClassParser parser, Type type) {

        return RamlTypeFactory.forType(type, parser, adjusterFactory);
    }

    public static TypeBuilder ramlStringType() {

        return TypeBuilder.type("string");
    }
}
