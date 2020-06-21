package org.raml.builder;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.DataNode;
import amf.client.model.domain.ScalarShape;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class AnyShapeBuilder extends TypeShapeBuilder<ScalarShape, AnyShapeBuilder> {

    private List<DataNode> enumValues;

    public AnyShapeBuilder() {
    }


    @Override
    public AnyShape buildNode() {

        AnyShape shape = new AnyShape();
        commonNodeInfo(shape);
        return shape;
    }

    public String id() {

        return "[any]";
    }
}
