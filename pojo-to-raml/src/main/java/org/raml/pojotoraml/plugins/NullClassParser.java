package org.raml.pojotoraml.plugins;

import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.Property;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class NullClassParser implements ClassParser {

    @Override
    public List<Property> properties(Class<?> sourceClass) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Type> parentClasses(Class<?> sourceClass) {
        return Collections.emptyList();
    }
}
