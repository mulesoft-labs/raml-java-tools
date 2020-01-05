package org.raml.pojotoraml.plugins;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.pojotoraml.Property;
import org.raml.pojotoraml.RamlAdjuster;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class AdditionalPropertiesAdjuster extends RamlAdjuster.Helper {

    @Override
    public TypeBuilder adjustForUnknownType(Type type) {

        if (type instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) type;
            if ( parameterizedType.getRawType() == Map.class &&
                    parameterizedType.getActualTypeArguments().length == 2 &&
                    parameterizedType.getActualTypeArguments()[0].equals(String.class)) {

                return null;
            }
        }

        return super.adjustForUnknownType(type);
    }

    @Override
    public void adjustForUnknownTypeInProperty(Type type, TypeBuilder typeBuilder, TypeDeclarationBuilder builder, Property property) {

        if (property.type() instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) property.type();
            if ( parameterizedType.getRawType() == Map.class &&
                    parameterizedType.getActualTypeArguments().length == 2 &&
                    parameterizedType.getActualTypeArguments()[0].equals(String.class)) {

                typeBuilder.withProperty(TypePropertyBuilder.property("//", "string"));
                return;
            }
        }
        super.adjustForUnknownTypeInProperty(type, typeBuilder, builder, property);
    }
}
