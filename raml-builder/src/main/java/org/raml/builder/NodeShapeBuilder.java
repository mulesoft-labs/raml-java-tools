package org.raml.builder;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.NodeShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class NodeShapeBuilder extends TypeShapeBuilder<NodeShape, NodeShapeBuilder> {


    private List<TypeShapeBuilder<?,?>> types;

    private final List<PropertyShapeBuilder> properties = new ArrayList<>();

    public NodeShapeBuilder(TypeShapeBuilder<?,?>... types) {

        this.types = Arrays.asList(types);
    }

    private AnyShape doInheritance(TypeShapeBuilder<?,?> t) {
        System.err.println("inheriting " + t.currentName());
        AnyShape anyShape = t.buildReference();
        anyShape.withLinkTarget(t.buildNode());
        anyShape.withLinkLabel(t.buildNode().name().value());

        return anyShape;
    }


    public NodeShapeBuilder withProperty(PropertyShapeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }


    @Override
    protected NodeShape buildNodeLocally() {

        NodeShape nodeShape = new NodeShape();
        commonNodeInfo(nodeShape);
        nodeShape.withName("anonymous");

        if ( types != null && types.size() != 0) {
                //Not sure....
                nodeShape.withInherits(types.stream().map(this::doInheritance).collect(Collectors.toList()));
        }

        if ( ! properties.isEmpty() ) {

            nodeShape.withProperties(properties.stream().map(PropertyShapeBuilder::buildNode).collect(Collectors.toList()));
        }

        return nodeShape;
    }

    @Override
    protected NodeShape buildReferenceShape() {
        return new NodeShape();
    }
}
