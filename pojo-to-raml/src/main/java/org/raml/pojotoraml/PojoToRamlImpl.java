package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.RamlTypeFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created. There, you have it.
 */
public class PojoToRamlImpl implements PojoToRaml {

    private final ClassParserFactory classParserFactory;
    private final RamlAdjuster adjuster;

    public PojoToRamlImpl(ClassParserFactory parser, RamlAdjuster adjuster) {
        this.classParserFactory = parser;
        this.adjuster = adjuster;
    }

    @Override
    public Result classToRaml(Class<?> clazz) {

        RamlType type = RamlTypeFactory.forType(clazz, classParserFactory.createParser(clazz), adjuster);

        if ( type.isScalar()) {

            return new Result(null, Collections.<String, TypeDeclarationBuilder>emptyMap());
        }

        Map<String, TypeDeclarationBuilder> dependentTypes = new HashMap<>();
        TypeDeclarationBuilder builder = handleSingleType(classParserFactory.createParser(clazz), adjuster, dependentTypes);
        dependentTypes.remove(builder.id());
        return new Result(builder, dependentTypes);
    }

    private TypeDeclarationBuilder handleSingleType(ClassParser parser, RamlAdjuster adjuster, Map<String, TypeDeclarationBuilder> builtTypes) {

        RamlType quickType = exploreType(parser, parser.underlyingClass(), adjuster);
        if ( quickType.isScalar()) {

            return TypeDeclarationBuilder.typeDeclaration(quickType.getRamlSyntax());
        }

        final String simpleName = adjuster.adjustTypeName(parser.underlyingClass().getSimpleName(), parser);

        TypeBuilder builder = buildSuperType(parser, adjuster, builtTypes);

        builder = adjuster.adjustType(builder);
        TypeDeclarationBuilder typeDeclaration = TypeDeclarationBuilder.typeDeclaration(simpleName).ofType(builder);
        builtTypes.put(simpleName, typeDeclaration);

        for (Property property : parser.properties()) {

            RamlType ramlType = exploreType(parser, property.type(), adjuster);

            if ( ramlType.isScalar() ) {
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                builder.withProperty(adjuster.adjustProperty(typePropertyBuilder));
            } else {

                ClassParser subParser = classParserFactory.createParser(ramlType.type());

                final String subSimpleName = adjuster.adjustTypeName(ramlType.type().getSimpleName(), parser);
                if ( ! builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(subParser, adjuster, builtTypes);
                }
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                builder.withProperty(adjuster.adjustProperty(typePropertyBuilder));
            }
        }

        return typeDeclaration;
    }

    private TypeBuilder buildSuperType(ClassParser parser, RamlAdjuster adjuster, Map<String, TypeDeclarationBuilder> builtTypes) {
        Collection<Type> types = parser.parentClasses();
        ArrayList<String> typeNames = new ArrayList<>();
        for (Type supertype : types) {

            RamlType ramlType = exploreType(parser, supertype, adjuster);

            ClassParser subParser = classParserFactory.createParser((ramlType.type()));
            final String subSimpleName = adjuster.adjustTypeName(ramlType.type().getSimpleName(), parser);
            if ( ! builtTypes.containsKey(subSimpleName)) {

                handleSingleType(subParser, adjuster, builtTypes);
            }

            typeNames.add(subSimpleName);
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
