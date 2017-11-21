package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class GenerationContextImpl implements GenerationContext {

    private final TypeFetcher typeFetcher;
    private final Map<String, CreationResult> knownTypes = new HashMap<>();

    public GenerationContextImpl() {
        this(TypeFetcher.NULL_FETCHER);
    }

    public GenerationContextImpl(TypeFetcher typeFetcher) {
        this.typeFetcher = typeFetcher;
    }

    @Override
    public CreationResult findCreatedType(TypeDeclaration ramlType) {

        if ( knownTypes.containsKey(ramlType.name()) ) {

            return knownTypes.get(ramlType.name());
        } else {

            TypeDeclaration typeDeclaration = typeFetcher.fetchType(ramlType.type());
            return TypeDeclarationType.typeHandler(typeDeclaration).create(this);
        }
    }
}
