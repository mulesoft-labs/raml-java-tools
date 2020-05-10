package org.raml.ramltopojo;

import amf.client.model.Annotable;

/**
 * Created. There, you have it.
 */
public interface AnnotationUser<T> {


    T getWithContext(Annotable target, Annotable... others);

    default T getValueWithDefault(T def, Annotable annotable, Annotable... others) {

        T t = getWithContext(annotable, others);
        if (t == null) {

            return def;
        } else {
            return t;
        }
    }

    default T get(T def, Annotable type) {

        return getValueWithDefault(def, type);
    }

    default T get(Annotable type) {

        return getValueWithDefault(null, type);
    }

    default T get(T defaultValue, Annotable type, Annotable... others) {

        return getValueWithDefault(defaultValue, type, others);
    }
}
