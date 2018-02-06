package org.raml.ramltopojo.references;

import com.google.common.base.Optional;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;

/**
 * Created. There, you have it.
 */
public class ReferenceTypeHandler implements TypeHandler {

    private final TypeDeclaration typeDeclaration;
    private final Class type;
    private final TypeName referenceName;

    public ReferenceTypeHandler(TypeDeclaration typeDeclaration, Class type, TypeName referenceName) {
        this.typeDeclaration = typeDeclaration;
        this.type = type;
        this.referenceName = referenceName;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType eventType) {
        throw new GenerationException("won't generate name for " + type.getSimpleName() + " class");
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {

        return generationContext.pluginsForReferences(
                    Utils.allParents(typeDeclaration, new ArrayList<TypeDeclaration>()).toArray(new TypeDeclaration[0]))
                .typeName(new ReferencePluginContext() {
                }, typeDeclaration, referenceName);
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

        throw new GenerationException("won't generate " + type.getSimpleName() + " class");
    }
}
