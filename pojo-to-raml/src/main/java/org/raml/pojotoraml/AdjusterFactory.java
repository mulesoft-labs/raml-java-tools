package org.raml.pojotoraml;

/**
 * Created. There, you have it.
 */
public interface AdjusterFactory {

    AdjusterFactory NULL_FACTORY = clazz -> RamlAdjuster.NULL_ADJUSTER;

    RamlAdjuster createAdjuster(Class<?> clazz);
}
