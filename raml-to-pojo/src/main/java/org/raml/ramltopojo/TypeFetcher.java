package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public interface TypeFetcher {

    TypeFetcher NULL_FETCHER = new TypeFetcher() {
        @Override
        public TypeDeclaration fetchType(String name) throws GenerationException {
            throw new GenerationException("null fetcher can't fetch types");
        }
    };

    TypeDeclaration fetchType(String name) throws GenerationException;
}
