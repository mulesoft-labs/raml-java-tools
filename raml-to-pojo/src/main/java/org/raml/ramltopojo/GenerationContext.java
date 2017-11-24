package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public interface GenerationContext {

    CreationResult findCreatedType(String typeName, TypeDeclaration ramlType);
    String defaultPackage();
}
