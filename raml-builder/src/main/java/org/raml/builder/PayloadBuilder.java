package org.raml.builder;


import amf.client.model.domain.Payload;
import amf.client.model.domain.Shape;

/**
 * Created. There, you have it.
 */
public class PayloadBuilder extends DomainElementBuilder<PayloadBuilder> implements NodeBuilder {

    private final String name;
    private TypeShapeBuilder types = TypeShapeBuilder.anyType();

    private PayloadBuilder(String name) {
        super();
        this.name = name;
    }

    static public PayloadBuilder body(String type) {

        return new PayloadBuilder(type);
    }

    public PayloadBuilder ofType(TypeShapeBuilder builder) {

        types = builder;
        return this;
    }

    @Override
    public Payload buildNode() {

        Payload payload = new Payload();
        payload.withSchema((Shape) types.buildNode());
        payload.withMediaType(name);

        return payload;
    }
}
