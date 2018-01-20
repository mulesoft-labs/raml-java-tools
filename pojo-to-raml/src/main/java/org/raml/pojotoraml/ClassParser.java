package org.raml.pojotoraml;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Created. There, you have it.
 */
public interface ClassParser {

    Class<?> underlyingClass();
    List<Property> properties();
    Collection<Type> parentClasses();

    ClassParser parseDependentClass(Class<?> type);
}
