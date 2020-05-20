package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
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

        List<NamedType> allShapes = generationContext.allKnownTypes();
        allShapes.stream()
                .filter(a ->  ! ExtraInformationImpl.isInline(a.shape()))
                .forEach( a ->  {

                    TypeName t = ShapeType.calculateTypeName(a.shape().name().value(), a.shape(), generationContext, EventType.INTERFACE);
                    generationContext.newTypeName(
                            a.shape(), t
                           );
                });


        allShapes
                .forEach( a -> {
                    Optional<CreationResult> spec = CreationResultFactory.createType(a.shape(), generationContext);
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
        TypeName typeName =  ShapeType.calculateTypeName(suggestedName, namedType.shape(), generationContext, EventType.INTERFACE);
        namedType.nameType(typeName);

        return typeName;
    }

    @Override
    public Optional<TypeName> fetchTypeName(AnyShape anyShape) {
        return generationContext.findTypeNameByTypeId(anyShape);
    }
}
