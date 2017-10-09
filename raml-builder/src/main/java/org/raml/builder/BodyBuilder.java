package org.raml.builder;

import org.raml.yagi.framework.nodes.Node;

/**
 * Created. There, you have it.
 */
public class BodyBuilder extends KeyValueNodeBuilder<BodyBuilder> implements NodeBuilder {

    private TypeBuilder types = null;

    private BodyBuilder(String name) {
        super(name);
    }

    static public BodyBuilder body(String type) {

        return new BodyBuilder(type);
    }

    public BodyBuilder ofType(TypeBuilder builder) {

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
