package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public interface GenerationContext {

    CreationResult findCreatedType(String typeName, TypeDeclaration ramlType);
    String defaultPackage();

    void createTypes(String rootDirectory) throws IOException;

    Api api();
}
