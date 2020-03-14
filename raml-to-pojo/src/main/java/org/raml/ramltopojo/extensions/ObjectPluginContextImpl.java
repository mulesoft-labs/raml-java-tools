package org.raml.ramltopojo.extensions;

import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.ShapeType;

import java.util.Set;
import java.util.stream.Collectors;

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

        return generationContext.childClasses(ramlTypeName).stream()
                .map((input) -> generationContext.findCreatedType(input, null))
                .collect(Collectors.toSet());
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
    public TypeName forProperty(PropertyShape typeDeclaration) {
        return ShapeType.calculateTypeName("", null /*typeDeclaration*/, generationContext, EventType.INTERFACE);
    }

    @Override
    public TypeName createSupportClass(TypeSpec.Builder newSupportType) {

        return this.generationContext.createSupportClass(newSupportType);
    }
}
