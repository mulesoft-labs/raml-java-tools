package org.raml.pojotoraml;

import com.google.common.base.Optional;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.types.RamlType;
import org.raml.pojotoraml.types.ScalarType;

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

            Optional<RamlType> ramlType =  ScalarType.fromType(property.type());

            if ( ramlType.isPresent() ) {
                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), ramlType.get().getRamlSyntax());
                builder.withProperty(adjuster.adjustProperty(typePropertyBuilder));
            } else {


                ClassParser subParser = parser.parseDependentClass(property.type());

                final String subSimpleName = adjuster.adjustTypeName(property.type().getSimpleName(), parser);
                if ( ! builtTypes.containsKey(subSimpleName)) {

                    handleSingleType(subParser, adjuster);
                }

                TypePropertyBuilder typePropertyBuilder = TypePropertyBuilder.property(property.name(), subSimpleName);
                builder.withProperty(adjuster.adjustProperty(typePropertyBuilder));
            }
        }

        return new RamlType() {
            @Override
            public String getRamlSyntax() {
                return simpleName;
            }
        };
    }
}
