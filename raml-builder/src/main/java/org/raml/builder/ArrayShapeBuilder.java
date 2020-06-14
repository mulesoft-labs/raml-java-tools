package org.raml.builder;

import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.DomainElement;

/**
 * Created. There, you have it.
 */
public class ArrayShapeBuilder extends TypeShapeBuilder<ArrayShape, ArrayShapeBuilder> {


    private final String type;
    private final TypeShapeBuilder<?,?> arrayItems;

    public ArrayShapeBuilder(TypeShapeBuilder<?,?> builder) {
        this.type = "array";
        this.arrayItems = builder;
    }

    public String id() {

        return "[" + type + "]";
    }

    @Override
    public ArrayShape buildNode() {

        ArrayShape shape = new ArrayShape();
        commonNodeInfo(shape);
        if (  arrayItems != null  ) {

//            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("items"), arrayItems.buildNode());
//            node.addChild(kvn);
        }

        return shape;
    }
}
