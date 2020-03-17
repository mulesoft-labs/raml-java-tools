package org.raml.ramltopojo;

import amf.client.model.domain.Shape;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created. There, you have it.
 */
public class TypeFetchers {
    public static final TypeFetcher NULL_FETCHER = (api, name) -> {
        throw new GenerationException("null fetcher can't fetch types: " + name);
    };

    public static TypeFetcher fromTypes() {

        return (api, name) -> TypeFindingUtils.shapesFromTypes(api)
                .filter(namedPredicate(name)).findFirst().orElseThrow(fail(name));
    }

    protected static Supplier<GenerationException> fail(final String name) {
        return () -> new GenerationException("can't fetch type named " + name);
    }

    public static TypeFetcher fromLibraries() {

        return (api, name) -> TypeFindingUtils.shapesFromLibraries(api)
                .filter(namedPredicate(name)).findFirst().orElseThrow(fail(name));
    }

    public static TypeFetcher fromAnywhere() {

        return (api, name) -> {
            // todo resources
            return Stream.concat(TypeFindingUtils.shapesFromTypes(api), TypeFindingUtils.shapesFromLibraries(api))
                    .filter(namedPredicate(name))
                    .findFirst()
                    .orElseThrow(fail(name));
        };
    }

    private static Predicate<Shape> namedPredicate(final String name) {
        return input -> name.equals(input.name().value());
    }


}
