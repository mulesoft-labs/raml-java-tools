package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;

/**
 * Created. There, you have it.
 */
public interface PojoToRaml {

    Result classToRaml(Class<?> clazz);
    TypeBuilder name(Class<?> clazz);
}
