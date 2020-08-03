package org.raml.builder;


import amf.client.model.domain.Payload;

/**
 * Created. There, you have it.
 */
public class PayloadBuilder extends DomainElementBuilder<Payload, PayloadBuilder>  {

    private final String name;
    private TypeShapeBuilder<?,?> types = TypeShapeBuilder.anyType();

    private PayloadBuilder(String name) {
        super();
        this.name = name;
    }

    static public PayloadBuilder body(String type) {

        return new PayloadBuilder(type);
    }

    public PayloadBuilder ofType(TypeShapeBuilder<?,?> builder) {

        types = builder;
        return this;
    }

    @Override
    protected Payload buildNodeLocally() {

        Payload payload = new Payload();
        if ( types.currentName() == null ) {
            payload.withSchema(types.buildNode());
        } else {
            payload.withSchema(types.buildReference());
        }

        payload.withMediaType(name);

        return payload;
    }
}
