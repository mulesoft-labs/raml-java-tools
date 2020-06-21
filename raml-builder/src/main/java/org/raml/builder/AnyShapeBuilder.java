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
    public AnyShape buildNode() {

        AnyShape shape = new AnyShape();
        shape.withName(name);
        commonNodeInfo(shape);
        return shape;
    }

    public String id() {

        return "[any]";
    }
}
