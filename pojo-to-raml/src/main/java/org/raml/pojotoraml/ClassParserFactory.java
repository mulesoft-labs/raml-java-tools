package org.raml.pojotoraml;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public interface ClassParserFactory {

    ClassParser createParser(Type clazz);
}
