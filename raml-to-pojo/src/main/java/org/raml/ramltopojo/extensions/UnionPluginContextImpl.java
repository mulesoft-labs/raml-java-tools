package org.raml.ramltopojo.extensions;

import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.TypeDeclarationType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import com.squareup.javapoet.TypeName;

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
    public TypeName findType(String typeName, TypeDeclaration type) {

        return TypeDeclarationType.calculateTypeName(typeName, type, generationContext, EventType.INTERFACE);
    }

    @Override
    public CreationResult unionClass(TypeDeclaration ramlType) {
        return generationContext.findCreatedType(ramlType.name(), ramlType);
    }
}
