package org.raml.builder;

import amf.client.model.domain.NilShape;

/**
 * Created. There, you have it.
 */
public class NilShapeBuilder extends TypeShapeBuilder<NilShape, NilShapeBuilder> {

    @Override
    protected NilShape buildNodeLocally() {
        return new NilShape();
    }
}
