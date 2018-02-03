package org.raml.pojotoraml;

import com.google.common.base.Optional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public interface Property {

    /**
     * Returns the required annotation from the parsed class
     * @param annotationType
     * @param <T>
     * @return
     */
    <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType);

    /**
     * Returns the property name
     * @return
     */
    String name();

    /**
     * Returns the property type.
     * @return
     */
    Type type();
}
