package org.raml.pojotoraml;

/**
 * Created. There, you have it.
 */
public interface AdjusterFactory {

    AdjusterFactory NULL_FACTORY = new AdjusterFactory() {

        @Override
        public RamlAdjuster createAdjuster(Class<?> clazz) {
            return RamlAdjuster.NULL_ADJUSTER;
        }
    };

    RamlAdjuster createAdjuster(Class<?> clazz);
}
