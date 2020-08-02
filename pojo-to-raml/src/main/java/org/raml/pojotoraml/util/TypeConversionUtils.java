package org.raml.pojotoraml.util;

import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public class TypeConversionUtils {

    public static String typeToSimpleName(Type clazz) {

        return TypeUtils.getRawType(clazz, clazz).getSimpleName();
    }
}
