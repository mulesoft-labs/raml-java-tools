package org.raml.ramltopojo.javapoet;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class JavaPoetUtilities {
    static <T> void withCollectionOfSpecs(Collection<T> collection, UnaryOperator<Collection<T>> cloneListBuilder, Consumer<Collection<T>> copier, UnaryOperator<T> methodOperator) {
        withSuppliedCollectionOfSpecs(() -> collection, cloneListBuilder, copier, methodOperator);
    }

    static <T> void withSuppliedCollectionOfSpecs(Supplier<Collection<T>> collectionSupplier, UnaryOperator<Collection<T>> cloneListBuilder, Consumer<Collection<T>> copier, UnaryOperator<T> methodOperator) {
        Collection<T> collection = collectionSupplier.get();
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

    static <T> void withProvidedSingularMapOfSpecs(Supplier<Map<String, T>> collectionSupplier, UnaryOperator<Map<String, T>> cloneListBuilder, BiConsumer<String, T> copier, UnaryOperator<Map.Entry<String,T>> methodOperator) {
        Map<String, T> collection = collectionSupplier.get();
        Map<String, T> newList = cloneListBuilder.apply(collection);
        collection.clear();
        newList.entrySet().stream().map(methodOperator).filter(Objects::nonNull).forEach(e -> copier.accept(e.getKey(), e.getValue()));
    }

    static <T> Supplier<Collection<T>> privateCollectionField(Object object, String name) {

        try {

            Field f = object.getClass().getDeclaredField(name);
            f.setAccessible(true);
            Collection<T> fieldValue = (Collection<T>) f.get(object);
            return () -> fieldValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T> Supplier<Map<String, T>> privateStringMapField(Object object, String name) {

        try {

            Field f = object.getClass().getDeclaredField(name);
            f.setAccessible(true);
            Map<String, T> fieldValue = (Map<String, T>) f.get(object);
            return () -> fieldValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
