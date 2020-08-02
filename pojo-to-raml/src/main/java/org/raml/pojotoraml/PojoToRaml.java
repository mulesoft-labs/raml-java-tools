package org.raml.pojotoraml;

import org.raml.builder.TypeShapeBuilder;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public interface PojoToRaml {

    Result classToRaml(Type clazz);
    TypeShapeBuilder<?, ?> typeShapeBuilder(Type type);
}
