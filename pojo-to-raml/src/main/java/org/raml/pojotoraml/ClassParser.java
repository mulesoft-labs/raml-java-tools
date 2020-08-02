package org.raml.pojotoraml;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * The mechanism used to discover the structure of a given class.  Normally, ClassParsers receive
 * the parsed class in their constructors.
 */
public interface ClassParser {

    /**
     * Returns a non-null list of property objects representing the properties of the
     * parsed class.
     * @return
     * @param sourceClass the class being parsed
     */
    List<Property> properties(Type sourceClass);

    /**
     * Returns a a non-null list of supertypes (that will become RAML super types) of the parsed class.
     * @return
     * @param sourceClass the class being parsed
     */
    Collection<Type> parentClasses(Type sourceClass);
}
