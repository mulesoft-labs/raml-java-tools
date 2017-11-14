package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public interface TypeHandlerFactory {

    TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration);
}
