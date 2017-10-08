package org.raml.pojotoraml;

/**
 * Created. There, you have it.
 */
public interface ClassVisitor {

    ClassParser newType(Class<?> classSource);
    void newProperty(Property propertySource);
}
