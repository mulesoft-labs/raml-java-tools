package org.raml.pojotoraml;

/**
 * Created. There, you have it.
 */
public interface ClassParserFactory {

    ClassParser createParser(Class<?> clazz);
}
