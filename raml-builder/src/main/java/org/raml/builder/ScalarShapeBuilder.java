package org.raml.builder;

import amf.client.model.domain.ScalarShape;

/**
 * Created. There, you have it.
 */
public class ScalarShapeBuilder extends TypeShapeBuilder<ScalarShape, ScalarShapeBuilder> {

    private final String typeName;
    private final String scalarShape;

    public ScalarShapeBuilder(String typeName, String scalarShape) {
        this.typeName = typeName;
        this.scalarShape = scalarShape;
    }

    @Override
    protected ScalarShape buildNodeLocally() {

        ScalarShape shape = new ScalarShape();
        shape.withName(typeName);
        commonNodeInfo(shape);
        shape.withDataType(scalarShape);
        //shape.withFormat("foo");

        return shape;
    }

    @Override
    protected ScalarShape buildReferenceShape() {
        ScalarShape ss =  new ScalarShape();
        ss.withDataType(this.buildNode().dataType().value());
        return ss;
    }
}
