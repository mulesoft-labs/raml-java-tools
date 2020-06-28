package org.raml.builder;

import amf.client.model.domain.ScalarShape;

/**
 * Created. There, you have it.
 */
public class ScalarShapeBuilder extends TypeShapeBuilder<ScalarShape, ScalarShapeBuilder> {

    private final String scalarShape;

    public ScalarShapeBuilder(String scalarShape) {
        this.scalarShape = scalarShape;
    }

    @Override
    protected ScalarShape buildNodeLocally() {

        ScalarShape shape = new ScalarShape();
        shape.withName("string");
        commonNodeInfo(shape);
        shape.withDataType(scalarShape);

        return shape;
    }
}
