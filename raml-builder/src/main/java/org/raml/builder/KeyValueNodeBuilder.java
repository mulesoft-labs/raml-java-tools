package org.raml.builder;

import org.raml.yagi.framework.nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class KeyValueNodeBuilder<B extends KeyValueNodeBuilder> implements NodeBuilder {

    final private String id;
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

    public String id() {

        return id;
    }

    public void addProperty(Node valueNode, String name, String value) {

        if ( value != null ) {
            KeyValueNode baseUriNode = new KeyValueNodeImpl(new StringNodeImpl(name), new StringNodeImpl(value));
            valueNode.addChild(baseUriNode);
        }
    }

    public void addProperty(Node valueNode, String name, Boolean value) {

        if ( value != null ) {
            KeyValueNode baseUriNode = new KeyValueNodeImpl(new StringNodeImpl(name), new BooleanNode(value));
            valueNode.addChild(baseUriNode);
        }
    }

    public void addProperty(Node valueNode, String name, Long value) {

        if ( value != null ) {
            KeyValueNode baseUriNode = new KeyValueNodeImpl(new StringNodeImpl(name), new NumberNode(value));
            valueNode.addChild(baseUriNode);
        }
    }

}
