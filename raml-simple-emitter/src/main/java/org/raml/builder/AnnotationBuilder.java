package org.raml.builder;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class AnnotationBuilder extends KeyValueNodeBuilder<TypeDeclaration, AnnotationBuilder> implements NodeBuilder {

    private AnnotationBuilder(String name) {
        super(name);
    }

    static public AnnotationBuilder annotation(String name) {

        return new AnnotationBuilder(name);
    }

    public TypeDeclaration build() {

        return super.build(TypeDeclaration.class, buildNode());
    }
}
