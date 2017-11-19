package org.raml.ramltopojo;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created. There, you have it.
 */
public class Scalars {

    public static TypeName classToTypeName(Class scalar) {
        if (scalar.isPrimitive()) {
            switch (scalar.getSimpleName()) {
                case "int":
                    return TypeName.INT;

                case "boolean":
                    return TypeName.BOOLEAN;

                case "double":
                    return TypeName.DOUBLE;

                case "float":
                    return TypeName.FLOAT;

                case "byte":
                    return TypeName.BYTE;

                case "char":
                    return TypeName.CHAR;

                case "short":
                    return TypeName.SHORT;

                case "long":
                    return TypeName.LONG;

                case "void":
                    return TypeName.VOID; // ?

                default:
                    throw new GenerationException("can't handle type: " + scalar);
            }
        } else {
            return ClassName.get(scalar);
        }
    }
}


