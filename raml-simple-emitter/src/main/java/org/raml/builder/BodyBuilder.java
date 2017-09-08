package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class BodyBuilder extends KeyValueNodeBuilder<BodyBuilder> implements NodeBuilder {

    private final String name;
    private TypeBuilder types = null;

    private BodyBuilder(String name) {
        super(name);
        this.name = name;
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
