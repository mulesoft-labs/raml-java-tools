package org.raml.pojotoraml;

import org.raml.builder.DeclaredShapeBuilder;

import java.util.*;

/**
 * Created. There, you have it.
 */
public class Result {

    final private DeclaredShapeBuilder<?> requestedType;
    final Collection<DeclaredShapeBuilder<?>> dependentTypes;

    public Result(DeclaredShapeBuilder<?> requestedType, Map<String, DeclaredShapeBuilder<?>> dependentTypes) {
        this.requestedType = requestedType;
        this.dependentTypes = dependentTypes.values();
    }

    public DeclaredShapeBuilder<?> requestedType() {
        return requestedType;
    }

    public Collection<DeclaredShapeBuilder<?>> dependentTypes() {
        return dependentTypes;
    }

    public Collection<DeclaredShapeBuilder<?>> allTypes() {
        if ( requestedType == null ) {
            return Collections.emptyList();
        } else {
            List<DeclaredShapeBuilder<?>> list = new ArrayList<>();
            list.add(requestedType);
            list.addAll(dependentTypes);
            return list;
        }
    }
}
