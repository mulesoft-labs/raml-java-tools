package org.raml.ramltopojo.nulltype;

import com.google.common.base.Optional;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.TypeHandler;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class NullTypeHandler implements TypeHandler {

    private final String name;
    private final TypeDeclaration typeDeclaration;

    public NullTypeHandler(String name, TypeDeclaration typeDeclaration) {

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
        return Optional.absent();
    }
}
