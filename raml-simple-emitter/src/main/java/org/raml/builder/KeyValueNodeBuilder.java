package org.raml.builder;

import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.yagi.framework.model.*;
import org.raml.yagi.framework.nodes.*;
import org.raml.yagi.framework.nodes.snakeyaml.SYIntegerNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class KeyValueNodeBuilder<B extends KeyValueNodeBuilder> implements NodeBuilder {

    private String id;
    private NodeBuilder[] builders = new NodeBuilder[0];

    protected KeyValueNodeBuilder(String name) {
        this.id = name;
    }

    protected KeyValueNodeBuilder(Long value) {
        this.id = value.toString();
    }

    public B with(NodeBuilder... builders) {

        this.builders = builders;
        return (B) this;
    }

    protected Node createValueNode() {

        return new ObjectNodeImpl();
    }
    public KeyValueNode buildNode() {
        Node value = createValueNode();
        for (NodeBuilder builder : builders) {
            value.addChild(builder.buildNode());
        }

        return new KeyValueNodeImpl(new StringNodeImpl(id), value);
    }

}
