package org.raml.ramltopojo.extensions;

import amf.client.model.domain.Shape;
import org.raml.ramltopojo.CreationResult;

/**
 * Created. There, you have it.
 */
public interface UnionPluginContext {

    CreationResult creationResult();
    CreationResult unionClass(Shape ramlType);
}
