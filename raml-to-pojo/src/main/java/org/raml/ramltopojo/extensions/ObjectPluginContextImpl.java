package org.raml.ramltopojo.extensions;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.*;

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
    public Set<TypeName> childClasses(String typeId) {

        return generationContext.childClasses(typeId).stream()
                .map((input) -> generationContext.findTypeNameByTypeId(input).orElseThrow(() -> new GenerationException("unable to find type id " + input.id())))
                .collect(Collectors.toSet());
    }

    @Override
    public CreationResult creationResult() {

        return result;
    }

    @Override
    public CreationResult dependentType(AnyShape items) {
        return generationContext.findCreatedType(items);
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
