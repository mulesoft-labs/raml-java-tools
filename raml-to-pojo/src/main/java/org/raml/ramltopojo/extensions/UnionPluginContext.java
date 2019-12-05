package org.raml.ramltopojo.extensions;

import org.raml.ramltopojo.CreationResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.squareup.javapoet.TypeName;
/**
 * Created. There, you have it.
 */
public interface UnionPluginContext {

    CreationResult creationResult();
    CreationResult unionClass(TypeDeclaration ramlType);
    TypeName findType(String typeName, TypeDeclaration type);
}
