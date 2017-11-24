package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class Utils {

    public static Class<?> declarationType(TypeDeclaration typeDeclaration) {

        return typeDeclaration.getClass().getInterfaces()[0];
    }
}
