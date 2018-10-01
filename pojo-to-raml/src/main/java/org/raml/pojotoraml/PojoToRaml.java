package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public interface PojoToRaml {

    Result classToRaml(Class<?> clazz);
    TypeBuilder name(Class<?> clazz);
    TypeBuilder name(Type type);
}
