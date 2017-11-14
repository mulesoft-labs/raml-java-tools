package org.raml.ramltopojo.enumeration;

import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.TypeHandler;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;

/**
 * Created. There, you have it.
 */
public class EnumerationTypeHandler implements TypeHandler {

    private final StringTypeDeclaration typeDeclaration;

    public EnumerationTypeHandler(StringTypeDeclaration stringTypeDeclaration) {

        this.typeDeclaration = stringTypeDeclaration;
    }

    @Override
    public TypeSpec create() {
        return null;
    }
}
