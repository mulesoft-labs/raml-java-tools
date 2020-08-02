package org.raml.pojotoraml;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public interface AdjusterFactory {

    AdjusterFactory NULL_FACTORY = clazz -> RamlAdjuster.NULL_ADJUSTER;

    RamlAdjuster createAdjuster(Type clazz);
}
