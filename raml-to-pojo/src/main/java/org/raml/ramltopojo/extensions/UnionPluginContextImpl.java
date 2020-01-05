package org.raml.ramltopojo.extensions;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.ShapeType;

/**
 * Created. There, you have it.
 */
public class UnionPluginContextImpl implements UnionPluginContext {
    private final GenerationContext generationContext;
    private final CreationResult result;

    public UnionPluginContextImpl(GenerationContext generationContext, CreationResult result) {
        this.generationContext = generationContext;
        this.result = result;
    }

    @Override
    public CreationResult creationResult() {

        return result;
    }

    @Override
    public TypeName findType(String typeName, AnyShape type) {

        return ShapeType.calculateTypeName(typeName, type, generationContext, EventType.INTERFACE);
    }

    @Override
    public CreationResult unionClass(AnyShape ramlType) {
        return generationContext.findCreatedType(ramlType.name().value(), ramlType);
    }
}
