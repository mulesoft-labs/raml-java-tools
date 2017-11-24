package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created. There, you have it.
 */
public class GenerationContextImpl implements GenerationContext {

    private final TypeFetcher typeFetcher;
    private final ConcurrentHashMap<String, CreationResult> knownTypes = new ConcurrentHashMap<>();

    public GenerationContextImpl() {
        this(TypeFetcher.NULL_FETCHER);
    }

    public GenerationContextImpl(TypeFetcher typeFetcher) {
        this.typeFetcher = typeFetcher;
    }

    @Override
    public CreationResult findCreatedType(String typeName, TypeDeclaration ramlType) {

        if ( knownTypes.containsKey(typeName) ) {

            return knownTypes.get(typeName);
        } else {

            TypeDeclaration typeDeclaration = typeFetcher.fetchType(typeName);
            CreationResult result =  TypeDeclarationType.typeHandler(typeDeclaration).create(this);
            knownTypes.put(typeName, result);
            return result;
        }
    }
}
