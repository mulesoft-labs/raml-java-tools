package org.raml.builder;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class ExamplesBuilder extends KeyValueNodeBuilder<TypeDeclaration, ExamplesBuilder> implements NodeBuilder {

    private ExamplesBuilder(String name) {
        super(name);
    }

    static public ExamplesBuilder examples(String name) {

        return new ExamplesBuilder(name);
    }

    public TypeDeclaration build() {

        return super.build(TypeDeclaration.class, buildNode());
    }
}
