package org.raml.ramltopojo.extensions;

import amf.client.model.domain.Shape;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.TypeDeclarationType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class ObjectPluginContextImpl implements ObjectPluginContext {
    private final GenerationContext generationContext;
    private final CreationResult result;

    public ObjectPluginContextImpl(GenerationContext generationContext, CreationResult result) {
        this.generationContext = generationContext;
        this.result = result;
    }

    @Override
    public Set<CreationResult> childClasses(String ramlTypeName) {

        return FluentIterable.from(generationContext.childClasses(ramlTypeName)).transform(new Function<String, CreationResult>() {
            @Nullable
            @Override
            public CreationResult apply(@Nullable String input) {
                return generationContext.findCreatedType(input, null);
            }
        }).toSet();
    }

    @Override
    public CreationResult creationResult() {

        return result;
    }

    @Override
    public CreationResult dependentType(Shape items) {
        return generationContext.findCreatedType(items.name().value(), items);
    }

    @Override
    public TypeName forProperty(TypeDeclaration typeDeclaration) {
        return TypeDeclarationType.calculateTypeName("", null /*typeDeclaration*/, generationContext, EventType.INTERFACE);
    }

    @Override
    public TypeName createSupportClass(TypeSpec.Builder newSupportType) {

        return this.generationContext.createSupportClass(newSupportType);
    }
}
