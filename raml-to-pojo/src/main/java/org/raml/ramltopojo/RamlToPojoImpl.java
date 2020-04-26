package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.google.common.collect.Lists;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class RamlToPojoImpl implements RamlToPojo {
    private final TypeFinder typeFinder;
    private final GenerationContextImpl generationContext;

    public RamlToPojoImpl( TypeFinder typeFinder, GenerationContextImpl generationContext) {

        this.typeFinder = typeFinder;
        this.generationContext = generationContext;
    }

    @Override
    public ResultingPojos buildPojos() {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

        List<AnyShape> allShapes = Lists.newArrayList(typeFinder.findTypes(generationContext.api()));
        allShapes.stream()
                .filter(a ->  ! ExtraInformation.isInline(a))
                .forEach( a -> generationContext.newTypeName(
                        a.name().value(),
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
    public ResultingPojos buildPojo(String suggestedJavaName, AnyShape typeDeclaration) {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

        Optional<CreationResult> spec = CreationResultFactory.createNamedType(suggestedJavaName, typeDeclaration, generationContext);
        spec.ifPresent(resultingPojos::addNewResult);

        return resultingPojos;
    }

    @Override
    public TypeName fetchTypeName(String suggestedName, AnyShape typeDeclaration) {


        return ShapeType.calculateTypeName(suggestedName, typeDeclaration, generationContext, EventType.INTERFACE);
    }

    public boolean isInline(AnyShape typeDeclaration) {

        return ShapeType.isNewInlineType(typeDeclaration);
    }
}
