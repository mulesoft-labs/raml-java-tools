package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypePropertyBuilder;

/**
 * Created. There, you have it.
 */
public interface RamlAdjuster {

    RamlAdjuster NULL_ADJUSTER = new RamlAdjuster() {

        @Override
        public TypeBuilder adjustType(TypeBuilder builder) {
            return builder;
        }

        @Override
        public String adjustTypeName(String name, ClassParser parser) {
            return name;
        }

        @Override
        public TypePropertyBuilder adjustProperty(TypePropertyBuilder typePropertyBuilder) {
            return typePropertyBuilder;
        }
    };

    TypeBuilder adjustType(TypeBuilder builder);
    String adjustTypeName(String name, ClassParser parser);
    TypePropertyBuilder adjustProperty(TypePropertyBuilder typePropertyBuilder);
}
