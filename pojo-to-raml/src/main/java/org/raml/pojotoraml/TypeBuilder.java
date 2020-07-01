package org.raml.pojotoraml;

import org.raml.builder.DeclaredShapeBuilder;


/**
 * Created. There, you have it.
 */
public interface TypeBuilder {

    DeclaredShapeBuilder<?> buildType(RamlAdjuster adjuster);
}
