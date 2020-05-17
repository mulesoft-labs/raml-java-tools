package org.raml.ramltopojo.extensions;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.NodeShape;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.*;

import java.util.List;

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
        return generationContext.findCreatedType(ramlType.id());
    }

    @Override
    public TypeName unionClassName(String typeId) {
        return generationContext.findTypeNameByTypeId(typeId).orElseThrow(() -> new GenerationException("no such declared type " + typeId + " while generating union" ));
    }

    @Override
    public List<AnyShape> parentTypes(NodeShape otd) {

        return generationContext.shapeTool().parentShapes(otd);
    }
}
