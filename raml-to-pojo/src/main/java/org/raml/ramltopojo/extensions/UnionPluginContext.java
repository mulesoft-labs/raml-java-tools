package org.raml.ramltopojo.extensions;

import org.raml.ramltopojo.CreationResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public interface UnionPluginContext extends GenericPluginContext {

    CreationResult unionClass(TypeDeclaration ramlType);
}
