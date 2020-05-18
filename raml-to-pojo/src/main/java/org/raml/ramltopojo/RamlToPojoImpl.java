package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.google.common.collect.Lists;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.amf.ExtraInformationImpl;

import java.util.List;
import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class RamlToPojoImpl implements RamlToPojo {
    private final GenerationContextImpl generationContext;

    public RamlToPojoImpl( GenerationContextImpl generationContext) {

        this.generationContext = generationContext;
    }

    @Override
    public ResultingPojos buildPojos() {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

        List<AnyShape> allShapes = Lists.newArrayList(generationContext.allKnownTypes());
        allShapes.stream()
                .filter(a ->  ! ExtraInformationImpl.isInline(a))
                .forEach( a -> generationContext.newTypeName(
                        a,
                        ShapeType.calculateTypeName(a.name().value(), a, generationContext, EventType.INTERFACE)));

        allShapes
                .forEach( a -> {
                    Optional<CreationResult> spec = CreationResultFactory.createType(a, generationContext);
                    spec.ifPresent(resultingPojos::addNewResult);
                });

        return resultingPojos;
    }

    @Override
    public ResultingPojos buildPojo(AnyShape typeDeclaration) {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

        Optional<CreationResult> spec = CreationResultFactory.createType(typeDeclaration, generationContext);
        spec.ifPresent(resultingPojos::addNewResult);

        return resultingPojos;
    }

    @Override
    public ResultingPojos buildPojo(String suggestedJavaName, String typeId) {

        AnyShape shape = (AnyShape) generationContext.api()
                .findById(typeId)
                .filter(t -> t instanceof AnyShape).orElseThrow(() -> new GenerationException("no such type id: " + typeId));

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

        Optional<CreationResult> spec = CreationResultFactory.createNamedType(suggestedJavaName, shape, generationContext);
        spec.ifPresent(resultingPojos::addNewResult);

        return resultingPojos;
    }

    @Override
    public TypeName attributeTypeToName(String suggestedName, AnyShape anyShape) {

        // todo fix so we use generation context structures.
        NamedType namedType = generationContext.findTargetNamedShape(anyShape).orElseThrow(() -> new GenerationException("no type found"));
        namedType.nameType(suggestedName);
        return ShapeType.calculateTypeName(suggestedName, namedType.getShape(), generationContext, EventType.INTERFACE);
    }

    @Override
    public Optional<TypeName> fetchTypeName(AnyShape anyShape) {
        return generationContext.findTypeNameByTypeId(anyShape);
    }
}
