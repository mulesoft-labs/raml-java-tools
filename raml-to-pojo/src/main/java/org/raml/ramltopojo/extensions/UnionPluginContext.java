package org.raml.ramltopojo.extensions;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.CreationResult;

/**
 * Created. There, you have it.
 */
public interface UnionPluginContext {

    CreationResult creationResult();
    CreationResult unionClass(AnyShape ramlType);
    TypeName findType(String typeName, AnyShape type);
}
