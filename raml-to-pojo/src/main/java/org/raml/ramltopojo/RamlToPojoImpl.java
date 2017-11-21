package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class RamlToPojoImpl implements RamlToPojo {
    @Override
    public ResultingPojos buildPojos(TypeDeclaration typeDeclaration) {

        TypeHandler handler = TypeDeclarationType.typeHandler(typeDeclaration);
        CreationResult spec = handler.create(new GenerationContextImpl(TypeFetcher.NULL_FETCHER));
        return null;
    }
}
