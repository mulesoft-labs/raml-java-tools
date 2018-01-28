package org.raml.pojotoraml;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.RamlTypeFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created. There, you have it.
 */
public class PojoToRamlImpl implements PojoToRaml {

    private final ClassParserFactory classParserFactory;
    private final AdjusterFactory adjuster;

    public PojoToRamlImpl(ClassParserFactory parser, AdjusterFactory adjuster) {
        this.classParserFactory = parser;
        this.adjuster = adjuster;
    }

    @Override
    public Result classToRaml(Class<?> clazz) {

        RamlAdjuster adjuster = this.adjuster.createAdjuster(clazz);
        RamlType type = RamlTypeFactory.forType(clazz, classParserFactory.createParser(clazz), adjuster);

        if ( type.isScalar()) {

            return new Result(null, Collections.<String, TypeDeclarationBuilder>emptyMap());
        }

        Map<String, TypeDeclarationBuilder> dependentTypes = new HashMap<>();
        TypeDeclarationBuilder builder = handleSingleType(classParserFactory.createParser(clazz), adjuster, dependentTypes);
        dependentTypes.remove(builder.id());
        return new Result(builder, dependentTypes);
    }

    @Override
    public String name(Class<?> clazz) {

        RamlAdjuster adjuster = this.adjuster.createAdjuster(clazz);

        ClassParser parser = classParserFactory.createParser(clazz);
        RamlType type = RamlTypeFactory.forType(clazz, parser, adjuster);

        if ( type.isScalar()) {

            return type.getRamlSyntax();
        }

        final String simpleName = adjuster.adjustTypeName(parser.underlyingClass(), parser.underlyingClass().getSimpleName(), parser);
        return simpleName;
    }

    private TypeDeclarationBuilder handleSingleType(ClassParser parser, RamlAdjuster adjuster, Map<String, TypeDeclarationBuilder> builtTypes) {

        RamlType quickType = exploreType(parser, parser.underlyingClass(), adjuster);
        if ( quickType.isScalar()) {

            return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax());
        }

        if ( quickType.isEnum()) {

            return handleEnum(quickType, adjuster);
        }

        final String simpleName = adjuster.adjustTypeName(parser.underlyingClass(), parser.underlyingClass().getSimpleName(), parser);

        TypeBuilder builder = buildSuperType(parser, adjuster, builtTypes);

        builder = adjuster.adjustType(parser.underlyingClass(), builder);
        TypeDeclarationBuilder typeDeclaration = TypeDeclarationBuilder.typeDeclaration(simpleName).ofType(builder);
        builtTypes.put(simpleName, typeDeclaration);

        for (Property property : parser.properties()) {

            RamlType ramlType = exploreType(parser, property.type(), adjuster);

            if ( ramlType.isScalar() ) {
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                RamlAdjuster subAdjuster = this.adjuster.createAdjuster(ramlType.type());
                builder.withProperty(subAdjuster.adjustScalarProperty(typeDeclaration, property,  typePropertyBuilder));
            } else {

                ClassParser subParser = classParserFactory.createParser(ramlType.type());
                RamlAdjuster subAdjuster = this.adjuster.createAdjuster(subParser.underlyingClass());
                final String subSimpleName = adjuster.adjustTypeName(parser.underlyingClass(), ramlType.type().getSimpleName(), parser);
                if ( ! builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(subParser, subAdjuster, builtTypes);
                }
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                builder.withProperty(adjuster.adjustComposedProperty(typeDeclaration, property,  typePropertyBuilder));
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
        return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax()).ofType(typeBuilder);
    }

    private TypeBuilder buildSuperType(ClassParser parser, RamlAdjuster adjuster, Map<String, TypeDeclarationBuilder> builtTypes) {
        Collection<Type> types = parser.parentClasses();
        ArrayList<String> typeNames = new ArrayList<>();
        if ( types != null ) {
            for (Type supertype : types) {

                RamlType ramlType = exploreType(parser, supertype, adjuster);

                ClassParser subParser = classParserFactory.createParser((ramlType.type()));
                final String subSimpleName = adjuster.adjustTypeName(parser.underlyingClass(), ramlType.type().getSimpleName(), parser);
                if (!builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(subParser, adjuster, builtTypes);
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

    private RamlType exploreType(ClassParser parser, Type type, RamlAdjuster adjuster) {

        return RamlTypeFactory.forType(type, parser, adjuster);
    }

    public static TypeBuilder ramlStringType() {

        return TypeBuilder.type("string");
    }
}
