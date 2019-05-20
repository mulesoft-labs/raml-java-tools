package org.raml.ramltopojo;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
public interface TypeHandler {
    ClassName javaClassName(GenerationContext generationContext, EventType type);
    TypeName javaClassReference(GenerationContext generationContext, EventType type);

    Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult);
}
