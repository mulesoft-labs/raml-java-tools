package org.raml.pojotoraml;

import com.google.common.collect.FluentIterable;
import org.raml.builder.DeclaredShapeBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class Result {

    final private DeclaredShapeBuilder requestedType;
    final Collection<DeclaredShapeBuilder> dependentTypes;

    public Result(DeclaredShapeBuilder requestedType, Map<String, DeclaredShapeBuilder> dependentTypes) {
        this.requestedType = requestedType;
        this.dependentTypes = dependentTypes.values();
    }

    public DeclaredShapeBuilder requestedType() {
        return requestedType;
    }

    public Collection<DeclaredShapeBuilder> dependentTypes() {
        return dependentTypes;
    }

    public Collection<DeclaredShapeBuilder> allTypes() {
        if ( requestedType == null ) {
            return Collections.emptyList();
        } else {
            return FluentIterable.from(Collections.singleton(requestedType)).append(dependentTypes).toList();
        }
    }
}
