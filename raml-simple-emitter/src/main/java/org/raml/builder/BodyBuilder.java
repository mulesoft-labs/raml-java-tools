package org.raml.builder;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class BodyBuilder extends KeyValueNodeBuilder<TypeDeclaration, BodyBuilder> implements NodeBuilder {

    private BodyBuilder(String name) {
        super(name);
    }

    static public BodyBuilder annotation(String name) {

        return new BodyBuilder(name);
    }

    public TypeDeclaration build() {

        return super.build(TypeDeclaration.class, buildNode());
    }
}
