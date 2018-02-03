package org.raml.pojotoraml.field;

import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.ClassParserFactory;
import org.raml.pojotoraml.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class FieldClassParser implements ClassParser {

    private final Class<?> classSource;

    public FieldClassParser(Class<?> classSource) {
        this.classSource = classSource;
    }

    @Override
    public Class<?> underlyingClass() {
        return classSource;
    }

    @Override
    public List<Property> properties() {

        List<Property> props = new ArrayList<>();
        for (final Field field : classSource.getDeclaredFields()) {

            if (! Modifier.isTransient(field.getModifiers())) {
                Property prop = new FieldProperty(field);
                props.add(prop);
            }
        }
        return props;
    }

    @Override
    public Collection<Type> parentClasses() {

        ArrayList<Type> type = new ArrayList<>();
        if ( classSource.getSuperclass() != Object.class && classSource.getSuperclass() != null ) {
            type.add(classSource.getSuperclass());
        }
        return type;
    }

    public static FieldClassParser extractFieldsFrom(Class<?> classSource) {

        return new FieldClassParser(classSource);
    }

    public static ClassParserFactory factory() {
        return new ClassParserFactory() {
            @Override
            public ClassParser createParser(Class<?> clazz) {
                return new FieldClassParser(clazz);
            }
        };
    }
}
