package org.raml.builder;

import org.raml.yagi.framework.nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class KeyValueNodeBuilder<B extends KeyValueNodeBuilder> implements NodeBuilder {

    private String id;
    private List<NodeBuilder> builders = new ArrayList<>();

    protected KeyValueNodeBuilder(String name) {
        this.id = name;
    }

    protected KeyValueNodeBuilder(Long value) {
        this.id = value.toString();
    }

    public B with(NodeBuilder... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return (B) this;
    }

    protected Node createValueNode() {

        return new ObjectNodeImpl();
    }

    protected StringNodeImpl createKeyNode(String id) {
        return new StringNodeImpl(id);
    }

    public KeyValueNode buildNode() {
        Node value = createValueNode();
        for (NodeBuilder builder : builders) {
            value.addChild(builder.buildNode());
        }

        return new KeyValueNodeImpl(createKeyNode(id), value);
    }


}
