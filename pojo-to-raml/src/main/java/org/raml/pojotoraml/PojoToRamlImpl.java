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
        TypeDeclarationBuilder builder = handleSingleType(classParserFactory.createParser(clazz), dependentTypes);
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

        final String simpleName = adjuster.adjustTypeName(parser.underlyingClass(), parser.underlyingClass().getSimpleName(), parser);
        return TypeBuilder.type(simpleName);
    }

    private TypeDeclarationBuilder handleSingleType(ClassParser parser, Map<String, TypeDeclarationBuilder> builtTypes) {

        RamlType quickType = exploreType(parser, parser.underlyingClass());
        if ( quickType.isScalar()) {

            return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax().id()).ofType(quickType.getRamlSyntax());
        }

        if ( quickType.isEnum()) {

            return handleEnum(quickType, adjusterFactory.createAdjuster(parser.underlyingClass()));
        }

        final String simpleName = adjusterFactory.createAdjuster(parser.underlyingClass()).adjustTypeName(parser.underlyingClass(), parser.underlyingClass().getSimpleName(), parser);

        TypeBuilder builder = buildSuperType(parser, builtTypes);

        builder = adjusterFactory.createAdjuster(parser.underlyingClass()).adjustType(parser.underlyingClass(), builder);
        TypeDeclarationBuilder typeDeclaration = TypeDeclarationBuilder.typeDeclaration(simpleName).ofType(builder);
        if ( !ScalarType.isRamalScalarType(simpleName)) {
            builtTypes.put(simpleName, typeDeclaration);
        }

        for (Property property : parser.properties()) {

            RamlType ramlType = exploreType(parser, property.type());

            if ( ramlType.isScalar() ) {
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                builder.withProperty(adjusterFactory.createAdjuster(ramlType.type()).adjustScalarProperty(typeDeclaration, property,  typePropertyBuilder));
            } else {

                ClassParser subParser = classParserFactory.createParser(ramlType.type());
                final String subSimpleName = adjusterFactory.createAdjuster(parser.underlyingClass()).adjustTypeName(parser.underlyingClass(), ramlType.type().getSimpleName(), parser);
                if ( ! builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(subParser, builtTypes);
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

        adjuster.adjustType(quickType.type(), typeBuilder);
        return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax().id()).ofType(typeBuilder);
    }

    private TypeBuilder buildSuperType(ClassParser parser, Map<String, TypeDeclarationBuilder> builtTypes) {
        Collection<Type> types = parser.parentClasses();
        ArrayList<String> typeNames = new ArrayList<>();
        if ( types != null ) {
            for (Type supertype : types) {

                // Currently a workaround.
                if ( supertype instanceof Class && ((Class)supertype).getPackage().getName().startsWith("java.")) {
                    continue;
                }

                RamlType ramlType = exploreType(parser, supertype);

                ClassParser subParser = classParserFactory.createParser((ramlType.type()));
                final String subSimpleName = adjusterFactory.createAdjuster(parser.underlyingClass()).adjustTypeName(parser.underlyingClass(), ramlType.type().getSimpleName(), parser);
                if (!builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(subParser, builtTypes);
                }

                typeNames.add(subSimpleName);
            }
        }

        TypeBuilder builder;
        if ( typeNames.isEmpty()) {
            builder = TypeBuilder.type("object");
        } else {
            builder = TypeBuilder.type(typeNames.get(0));
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
