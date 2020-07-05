package org.raml.pojotoraml;

import org.raml.builder.DeclaredShapeBuilder;

import java.util.*;

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

    public List<DeclaredShapeBuilder<?>> findNamed(ArrayList<String> typeNames) {
        List<DeclaredShapeBuilder<?>> found = new ArrayList<>();

        typeNames.forEach(n -> Optional.ofNullable(seenByName.get(n)).ifPresent(found::add) );
        return found;
    }
}
