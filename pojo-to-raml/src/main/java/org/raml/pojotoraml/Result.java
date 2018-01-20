package org.raml.pojotoraml;

import com.google.common.collect.FluentIterable;
import org.raml.builder.TypeDeclarationBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class Result {

    final private TypeDeclarationBuilder requestedType;
    final Collection<TypeDeclarationBuilder> dependentTypes;

    public Result(TypeDeclarationBuilder requestedType, Map<String, TypeDeclarationBuilder> dependentTypes) {
        this.requestedType = requestedType;
        this.dependentTypes = dependentTypes.values();
    }

    public TypeDeclarationBuilder requestedType() {
        return requestedType;
    }

    public Collection<TypeDeclarationBuilder> dependentTypes() {
        return dependentTypes;
    }

    public Collection<TypeDeclarationBuilder> allTypes() {
        if ( requestedType == null ) {
            return Collections.emptyList();
        } else {
            return FluentIterable.from(Collections.singleton(requestedType)).append(dependentTypes).toList();
        }
    }
}
