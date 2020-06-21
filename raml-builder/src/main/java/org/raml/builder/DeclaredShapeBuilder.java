package org.raml.builder;


import amf.client.model.domain.AnyShape;

/**
 * Created. There, you have it.
 */
public class DeclaredShapeBuilder extends KeyValueNodeBuilder<DeclaredShapeBuilder> implements NodeBuilder {

    private final String name;
    private TypeShapeBuilder<AnyShape, ?> types = null;

    private DeclaredShapeBuilder(String name) {
        super(name);
        this.name = name;
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

        AnyShape shape = types.buildNode();
        shape.withName(name);
        return shape;
    }
}
