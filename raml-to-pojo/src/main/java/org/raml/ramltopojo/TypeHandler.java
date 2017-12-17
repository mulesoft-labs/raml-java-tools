package org.raml.ramltopojo;

import com.squareup.javapoet.ClassName;

/**
 * Created. There, you have it.
 */
public interface TypeHandler {
    ClassName javaTypeName(GenerationContext generationContext, EventType type);
    CreationResult create(GenerationContext generationContext, CreationResult preCreationResult);
}
