package org.raml.ramltopojo.javapoet;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class JavaPoetUtilities {
    static <T> void withCollectionOfSpecs(Collection<T> collection, UnaryOperator<Collection<T>> cloneListBuilder, Consumer<Collection<T>> copier, UnaryOperator<T> methodOperator) {
        Collection<T> newList = cloneListBuilder.apply(collection);
        collection.clear();
        copier.accept(newList.stream().map(methodOperator).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    static <T> void withEllipsisOfSpecs(Collection<T> collection, UnaryOperator<Collection<T>> cloneListBuilder, Consumer<T[]> copier, IntFunction<T[]> arrayBuilder, UnaryOperator<T> methodOperator) {
        Collection<T> newList = cloneListBuilder.apply(collection);
        collection.clear();
        copier.accept(newList.stream().map(methodOperator).filter(Objects::nonNull).toArray(arrayBuilder));
    }

    static <T> void withSingularMapOfSpecs(Map<String, T> collection, UnaryOperator<Map<String, T>> cloneListBuilder, BiConsumer<String, T> copier, UnaryOperator<Map.Entry<String,T>> methodOperator) {
        Map<String, T> newList = cloneListBuilder.apply(collection);
        collection.clear();
        newList.entrySet().stream().map(methodOperator).filter(Objects::nonNull).forEach(e -> copier.accept(e.getKey(), e.getValue()));
    }

}
