package org.raml;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.*;

/**
 * Created. There, you have it.
 */
public class UnbuildableTypeHandler  implements TypeHandler {

    private final Class type;
    private final TypeName referenceName;

    public UnbuildableTypeHandler(Class type, TypeName referenceName) {

        this.type = type;
        this.referenceName = referenceName;
    }
    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType eventType) {
        throw new GenerationException("won't generate "+ type.getSimpleName() + " class");
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        return referenceName;
    }

    @Override
    public CreationResult create(GenerationContext generationContext, CreationResult preCreationResult) {
        return null;
    }
}
