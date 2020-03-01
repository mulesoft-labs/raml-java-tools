package org.raml.ramltopojo;

import amf.client.model.domain.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class ExtraInformation {

    public static boolean isInline(Shape shape) {

        return shape.customDomainProperties().stream()
                .filter(x -> x.name().is("ramltopojo"))
                .findFirst()
                .map(x -> (ScalarNode)((ObjectNode)x.extension()).getProperty("inlined").orElse(ScalarTypes.SCALAR_NODE_FALSE))
                .orElse(ScalarTypes.SCALAR_NODE_FALSE).equals(ScalarTypes.SCALAR_NODE_TRUE);
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
        shape.withCustomDomainProperties(Collections.singletonList(de));

        // false inlined
        ScalarNode sn  = new ScalarNode("false", ScalarTypes.BOOLEAN_SCALAR);

        ObjectNode node = new ObjectNode();
        de.withExtension(node);
        URI shapeId = URI.create(shape.id());
        if (
                shape.isLink() ||
                shapeId.getFragment().matches("/declarations/types/[^/]*$") ||
                shapeId.getFragment().matches("/declarations/types/union/[^/]*$") ) {

            node.addProperty("inlined", ScalarTypes.SCALAR_NODE_FALSE);
        } else {

            node.addProperty("inlined", ScalarTypes.SCALAR_NODE_TRUE);
        }

        ArrayNode arrayNode  = new ArrayNode();
        if ( shape instanceof NodeShape ) {
            NodeShape nodeShape = (NodeShape) shape;
            nodeShape.inherits().forEach(i -> arrayNode.addMember(ScalarTypes.stringNode(i.name().value())) );
        }

        node.addProperty("supertypes", arrayNode);
    }
}
