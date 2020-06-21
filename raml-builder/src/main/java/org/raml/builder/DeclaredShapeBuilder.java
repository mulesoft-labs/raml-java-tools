package org.raml.builder;


import amf.client.model.domain.AnyShape;

/**
 * Created. There, you have it.
 */
public class DeclaredShapeBuilder extends KeyValueNodeBuilder<DeclaredShapeBuilder> implements NodeBuilder {

    private TypeShapeBuilder types = null;

    private DeclaredShapeBuilder(String name) {
        super(name);
    }

    static public DeclaredShapeBuilder typeDeclaration(String name) {

        return new DeclaredShapeBuilder(name);
    }

    public DeclaredShapeBuilder ofType(TypeShapeBuilder builder) {

        types = builder;
        return this;
    }

    @Override
    public AnyShape buildNode() {

            return null;
    }
}
