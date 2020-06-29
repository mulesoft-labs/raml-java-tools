package org.raml.builder;

import amf.client.model.domain.ArrayShape;

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

    @Override
    protected ArrayShape buildNodeLocally() {

        ArrayShape shape = new ArrayShape();
        commonNodeInfo(shape);
        if (  arrayItems != null  ) {

            shape.withItems(arrayItems.buildNode());
        }

        return shape;
    }
}
