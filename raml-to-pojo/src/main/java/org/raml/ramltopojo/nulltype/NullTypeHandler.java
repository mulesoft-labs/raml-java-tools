package org.raml.ramltopojo.nulltype;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.TypeHandler;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class NullTypeHandler implements TypeHandler {

    private final String name;
    private final Shape typeDeclaration;

    public NullTypeHandler(String name, Shape typeDeclaration) {

        this.name = name;
        this.typeDeclaration = typeDeclaration;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {
        return ClassName.get(Object.class);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        return ClassName.get(Object.class);
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {
        return Optional.empty();
    }
}
