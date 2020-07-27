package org.raml.builder;

import amf.client.model.domain.AnyShape;

/**
 * Created. There, you have it.
 */
public class AnyShapeBuilder extends TypeShapeBuilder<AnyShape, AnyShapeBuilder> {

    private final String name;

    public AnyShapeBuilder(String name) {
        this.name = name;
    }


    @Override
    protected AnyShape buildNodeLocally() {

        AnyShape shape = new AnyShape();
        shape.withName(name);
        commonNodeInfo(shape);
        return shape;
    }

    @Override
    protected AnyShape buildReferenceShape() {
        return new AnyShape();
    }
}
