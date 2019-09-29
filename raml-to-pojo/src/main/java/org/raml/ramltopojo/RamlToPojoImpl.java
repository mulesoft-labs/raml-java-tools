package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;

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

///*  TODO JP reactivate when types migrated
        for (AnyShape typeDeclaration : typeFinder.findTypes(generationContext.api())) {

            ShapeType.calculateTypeName(typeDeclaration.name().value(), typeDeclaration, generationContext, EventType.INTERFACE);
        }

        for (AnyShape typeDeclaration : typeFinder.findTypes(generationContext.api())) {

            Optional<CreationResult> spec = CreationResultFactory.createType(typeDeclaration, generationContext);
            spec.ifPresent(resultingPojos::addNewResult);
        }
//*/

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
    public TypeName fetchType(String suggestedName, AnyShape typeDeclaration) {


        return ShapeType.calculateTypeName(suggestedName, typeDeclaration, generationContext, EventType.INTERFACE);
    }

    public boolean isInline(AnyShape typeDeclaration) {

        return ShapeType.isNewInlineType(typeDeclaration);
    }
}
