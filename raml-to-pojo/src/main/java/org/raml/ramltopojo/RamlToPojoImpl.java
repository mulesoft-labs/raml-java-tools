package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class RamlToPojoImpl implements RamlToPojo {
    private final TypeFinder typeFinder;
    private final GenerationContextImpl generationContext;

    public RamlToPojoImpl(TypeFinder typeFinder, GenerationContextImpl generationContext) {

        this.typeFinder = typeFinder;
        this.generationContext = generationContext;
    }

    @Override
    public ResultingPojos buildPojos() {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);
        for (TypeDeclaration typeDeclaration : typeFinder.findTypes()) {

            TypeHandler handler = TypeDeclarationType.typeHandler(typeDeclaration);
            CreationResult spec = handler.create(generationContext);
            resultingPojos.addNewResult(spec);
        }

        return resultingPojos;
    }
}
