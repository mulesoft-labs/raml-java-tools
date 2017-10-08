package org.raml.pojotoraml.field;

import org.raml.pojotoraml.Property;

import java.lang.reflect.Field;

/**
 * Created. There, you have it.
 */
class FieldProperty implements Property {
    private final Field field;

    public FieldProperty(Field field) {
        this.field = field;
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public Class<?> type() {
        return field.getType();
    }
}
