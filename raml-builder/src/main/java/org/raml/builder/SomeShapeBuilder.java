package org.raml.builder;

import amf.client.model.domain.DomainElement;
import amf.client.model.domain.NodeShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class SomeShapeBuilder<B extends SomeShapeBuilder> implements NodeBuilder {

    private List<NodeBuilder> builders = new ArrayList<>();

    public B with(NodeBuilder... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return (B) this;
    }

    public DomainElement buildNode() {
        NodeShape value = new NodeShape();
        for (NodeBuilder builder : builders) {
            value.addChild(builder.buildNode());
        }

        return value;
    }

}
