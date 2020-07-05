package org.raml.pojotoraml;

import org.raml.builder.DeclaredShapeBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class SeenTypes {
    private final Map<String, DeclaredShapeBuilder<?>> seenByName = new HashMap<>();
    private final Map<String, DeclaredShapeBuilder<?>> seenById = new HashMap<>();

    public void storeType(DeclaredShapeBuilder<?> builder) {

        if ( ! builder.isAnonymous() ) {
            seenByName.put(builder.name(), builder);
        }

        seenById.put(builder.id(), builder);
    }

    public void remove(DeclaredShapeBuilder<?> builder) {

        if ( ! builder.isAnonymous() ) {
            seenByName.remove(builder.name(), builder);
        }

        seenById.remove(builder.id(), builder);
    }

    public Collection<DeclaredShapeBuilder<?>> namedTypes() {

        return seenByName.values();
    }

    public boolean hasName(String subSimpleName) {

        return seenByName.containsKey(subSimpleName);
    }

    public Map<String, DeclaredShapeBuilder<?>> namedAsMap() {
        return seenByName;
    }
}
