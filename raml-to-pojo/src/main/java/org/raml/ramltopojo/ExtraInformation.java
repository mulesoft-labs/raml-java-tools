package org.raml.ramltopojo;

import amf.client.model.domain.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class ExtraInformation {

    public static boolean isInline(Shape shape) {

        return shape.customDomainProperties().stream()
                .filter(x -> x.name().is("ramltopojo"))
                .findFirst()
                .map(x -> (ScalarNode)((ObjectNode)x.extension()).getProperty("inlined").orElse(ScalarTypes.SCALAR_NODE_TRUE))
                .orElse(ScalarTypes.SCALAR_NODE_TRUE).equals(ScalarTypes.SCALAR_NODE_TRUE);
    }

    public static List<String> parentTypes(Shape shape) {

        return shape.customDomainProperties().stream()
                .filter(x -> x.name().is("ramltopojo"))
                .findFirst()
                .map(x -> (ArrayNode)((ObjectNode)x.extension()).getProperty("supertypes").orElse(new ArrayNode()))
                .orElse(new ArrayNode()).members().stream().map(ScalarNode.class::cast).map(x -> x.value().value()).collect(Collectors.toList());
    }

    static void createInformation(AnyShape shape) {
        DomainExtension de = new DomainExtension().withName("ramltopojo");
        List<DomainExtension> newList = new ArrayList<>(shape.customDomainProperties());
        newList.add(de);
        shape.withCustomDomainProperties(newList);

        // false inlined
        ObjectNode node = new ObjectNode();
        de.withExtension(node);
        URI shapeId = URI.create(shape.id());
        if (! shape.isLink() ) {
            node.addProperty("location", ScalarTypes.stringNode(shapeId.getPath()));
        }

        if (
                shape.isLink() ||
                shapeId.getFragment().matches("/declarations/types/[^/]*$") ||
                shapeId.getFragment().matches("/declarations/types/scalar/[^/]*$") ||
                shapeId.getFragment().matches("/declarations/types/union/[^/]*$") ) {

            node.addProperty("inlined", ScalarTypes.SCALAR_NODE_FALSE);
        } else {

            node.addProperty("inlined", ScalarTypes.SCALAR_NODE_TRUE);
        }

        ArrayNode arrayNode  = new ArrayNode();
        if ( shape instanceof NodeShape ) {
            NodeShape nodeShape = (NodeShape) shape;
            nodeShape.inherits().stream().map(ExtraInformation::nodeToName).filter(Objects::nonNull).forEach(i -> arrayNode.addMember(ScalarTypes.stringNode(i)) );
        }

        node.addProperty("supertypes", arrayNode);
    }

    private static  String nodeToName(Shape nodeShape) {

        return Optional.ofNullable(nodeShape.name().value())
                .orElse( nodeShape.linkTarget().filter(ne-> ne instanceof NodeShape).map(ne -> (NodeShape) ne).map(ns -> ns.name().value()).orElse(null));
    }
}
