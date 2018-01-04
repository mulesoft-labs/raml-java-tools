package org.raml.ramltopojo;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created. There, you have it.
 */
public interface TypeHandler {
    ClassName javaClassName(GenerationContext generationContext, EventType type);
    TypeName javaClassReference(GenerationContext generationContext, EventType type);

    CreationResult create(GenerationContext generationContext, CreationResult preCreationResult);
}
