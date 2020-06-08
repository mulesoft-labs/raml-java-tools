package org.raml.pojotoraml;

import com.google.common.collect.FluentIterable;
import org.raml.builder.AnyShapeBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class Result {

    final private AnyShapeBuilder requestedType;
    final Collection<AnyShapeBuilder> dependentTypes;

    public Result(AnyShapeBuilder requestedType, Map<String, AnyShapeBuilder> dependentTypes) {
        this.requestedType = requestedType;
        this.dependentTypes = dependentTypes.values();
    }

    public AnyShapeBuilder requestedType() {
        return requestedType;
    }

    public Collection<AnyShapeBuilder> dependentTypes() {
        return dependentTypes;
    }

    public Collection<AnyShapeBuilder> allTypes() {
        if ( requestedType == null ) {
            return Collections.emptyList();
        } else {
            return FluentIterable.from(Collections.singleton(requestedType)).append(dependentTypes).toList();
        }
    }
}
