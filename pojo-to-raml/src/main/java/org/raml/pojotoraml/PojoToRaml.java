package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.types.ComposedRamlType;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.RamlTypeFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class PojoToRaml {

    private Map<String, TypeBuilder> builtTypes = new HashMap<>();

    public Map<String, TypeBuilder> pojoToRamlTypeBuilder(ClassParser parser, RamlAdjuster adjuster) {

        handleSingleType(parser, adjuster);

        return builtTypes;
    }

    private RamlType handleSingleType(ClassParser parser, RamlAdjuster adjuster) {
        final String simpleName = adjuster.adjustTypeName(parser.underlyingClass().getSimpleName(), parser);
        TypeBuilder builder = TypeBuilder.type(simpleName);
        builder = adjuster.adjustType(builder);
        builtTypes.put(simpleName, builder);

        for (Property property : parser.properties()) {

            RamlType ramlType = exploreTypeForProperty(parser, property.type(), adjuster);

            if ( ramlType.isScalar() ) {
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                builder.withProperty(adjuster.adjustProperty(typePropertyBuilder));
            } else {

                    ClassParser subParser = parser.parseDependentClass((ramlType.type()));

                    final String subSimpleName = adjuster.adjustTypeName(ramlType.type().getSimpleName(), parser);
                    if ( ! builtTypes.containsKey(subSimpleName)) {

                        handleSingleType(subParser, adjuster);
                    }
                    TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.getRamlSyntax());
                    builder.withProperty(adjuster.adjustProperty(typePropertyBuilder));
            }
        }

        return ComposedRamlType.forClass(parser.underlyingClass(), simpleName);
    }

    private RamlType exploreTypeForProperty(ClassParser parser, Type type, RamlAdjuster adjuster) {

        return RamlTypeFactory.forType(type, parser, adjuster);
    }
}
