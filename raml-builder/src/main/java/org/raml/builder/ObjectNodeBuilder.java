package org.raml.builder;

import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ObjectNodeBuilder<B extends ObjectNodeBuilder> implements NodeBuilder {

    private List<NodeBuilder> builders = new ArrayList<>();

    public B with(NodeBuilder... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return (B) this;
    }

    public ObjectNode buildNode() {
        ObjectNode value = new ObjectNodeImpl();
        for (NodeBuilder builder : builders) {
            value.addChild(builder.buildNode());
        }

        return value;
    }

}
