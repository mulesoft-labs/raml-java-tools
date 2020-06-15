package org.raml.builder;



/**
 * Created. There, you have it.
 */
public class PayloadBuilder extends KeyValueNodeBuilder<PayloadBuilder> implements NodeBuilder {

    private TypeShapeBuilder types = null;

    private PayloadBuilder(String name) {
        super(name);
    }

    static public PayloadBuilder body(String type) {

        return new PayloadBuilder(type);
    }

    public PayloadBuilder ofType(TypeShapeBuilder builder) {

        types = builder;
        return this;
    }

    @Override
    protected Node createValueNode() {
        if ( types != null ) {

            return types.buildNode();
        } else {

            return super.createValueNode();
        }
    }
}
