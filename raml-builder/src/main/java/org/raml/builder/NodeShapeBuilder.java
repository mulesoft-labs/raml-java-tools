package org.raml.builder;

import amf.client.model.domain.DomainElement;
import amf.client.model.domain.NodeShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class NodeShapeBuilder extends TypeShapeBuilder<NodeShape, NodeShapeBuilder> {

    public String[] types;

    private final List<PropertyShapeBuilder> properties = new ArrayList<>();


    public NodeShapeBuilder(String... types) {

        this.types = types;
    }


    public NodeShapeBuilder withProperty(PropertyShapeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }


    @Override
    public DomainElement buildNode() {

        NodeShape nodeShape = new NodeShape();
        commonNodeInfo(nodeShape);

        if ( types != null ) {
            if (types.length == 1) {
                nodeShape.withName(types[0]);
            } else {

                //Not sure....
                Arrays.stream(types).forEach(nodeShape::withInheritsObject);
            }
        }

        if ( ! properties.isEmpty() ) {

            nodeShape.withProperties(properties.stream().map(PropertyShapeBuilder::buildNode).collect(Collectors.toList()));
        }

        return nodeShape;
    }


    public String id() {

        if (types.length == 1) {
            return types[0];
        } else {

            return "[" + String.join(",", types) + "]";
        }
    }
}
