package org.raml.ramltopojo.references;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ReferencePluginContext;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class ReferenceTypeHandler implements TypeHandler {

    private final Shape typeDeclaration;
    private final Class type;
    private final TypeName referenceName;

    public ReferenceTypeHandler(Shape typeDeclaration, Class type, TypeName referenceName) {
        this.typeDeclaration = typeDeclaration;
        this.type = type;
        this.referenceName = referenceName;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType eventType) {
        return ClassName.get(type);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {

        return generationContext.pluginsForReferences(
                    Utils.allParents((AnyShape) typeDeclaration).toArray(new Shape[0]))
                .typeName(new ReferencePluginContext() {
                }, typeDeclaration, referenceName);

    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

        return Optional.empty();
    }
}
