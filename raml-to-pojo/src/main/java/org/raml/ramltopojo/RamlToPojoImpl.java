package org.raml.ramltopojo;

import com.squareup.javapoet.ClassName;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

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

        for (TypeDeclaration typeDeclaration : typeFinder.findTypes(generationContext.api())) {

            ClassName intf = TypeDeclarationType.typeName(typeDeclaration, generationContext, EventType.INTERFACE);
            ClassName impl = TypeDeclarationType.typeName(typeDeclaration, generationContext, EventType.IMPLEMENTATION);

            CreationResult creationResult = new CreationResult(generationContext.defaultPackage(), intf, impl);
            generationContext.newExpectedType(typeDeclaration.name(), creationResult);
        }

        for (TypeDeclaration typeDeclaration : typeFinder.findTypes(generationContext.api())) {

            CreationResult spec = TypeDeclarationType.createType(typeDeclaration, generationContext);
            resultingPojos.addNewResult(spec);
        }

        return resultingPojos;
    }
}
