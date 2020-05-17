package org.raml.ramltopojo.extensions;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.NodeShape;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.CreationResult;

import java.util.List;

/**
 * Created. There, you have it.
 */
public interface UnionPluginContext {

    CreationResult creationResult();
    CreationResult unionClass(AnyShape ramlType);
    TypeName findType(String typeName, AnyShape type);

    TypeName unionClassName(String ramlName);

    List<AnyShape> parentTypes(NodeShape otd);
}
