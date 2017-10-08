package org.raml.pojotoraml;

import java.util.List;

/**
 * Created. There, you have it.
 */
public interface ClassParser {

    Class<?> underlyingClass();
    List<Property> properties();
    ClassParser parseDependentClass(Class<?> type);
}
