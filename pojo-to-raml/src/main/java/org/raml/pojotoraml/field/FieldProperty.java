package org.raml.pojotoraml.field;

import com.google.common.base.Optional;
import org.raml.pojotoraml.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
class FieldProperty implements Property {
    private final Field field;

    public FieldProperty(Field field) {
        this.field = field;
    }

    /**
     * Returns the required annotation from the field
     * @param annotationType
     * @param <T>
     * @return
     */
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {

        return Optional.fromNullable(field.getAnnotation(annotationType));
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public Type type() {
        return field.getGenericType();
    }
}
