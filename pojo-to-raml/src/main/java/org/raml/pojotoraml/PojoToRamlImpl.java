package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.RamlTypeFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

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

        Map<String, TypeDeclarationBuilder> dependentTypes = new HashMap<>();
        TypeDeclarationBuilder builder = handleSingleType(classParserFactory.createParser(clazz), adjuster, dependentTypes);
        dependentTypes.remove(builder.id());
        return new Result(builder, dependentTypes);
    }

    private TypeDeclarationBuilder handleSingleType(ClassParser parser, RamlAdjuster adjuster, Map<String, TypeDeclarationBuilder> builtTypes) {
        final String simpleName = adjuster.adjustTypeName(parser.underlyingClass().getSimpleName(), parser);
        TypeBuilder builder = TypeBuilder.type("object");
        builder = adjuster.adjustType(builder);
        TypeDeclarationBuilder typeDeclaration = TypeDeclarationBuilder.typeDeclaration(simpleName).ofType(builder);
        builtTypes.put(simpleName, typeDeclaration);

        for (Property property : parser.properties()) {

            RamlType ramlType = exploreTypeForProperty(parser, property.type(), adjuster);

            if ( ramlType.isScalar() ) {
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                builder.withProperty(adjuster.adjustProperty(typePropertyBuilder));
            } else {

                    ClassParser subParser = parser.parseDependentClass((ramlType.type()));

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

    private RamlType exploreTypeForProperty(ClassParser parser, Type type, RamlAdjuster adjuster) {

        return RamlTypeFactory.forType(type, parser, adjuster);
    }

    public static TypeBuilder ramlStringType() {

        return TypeBuilder.type("string");
    }
}
