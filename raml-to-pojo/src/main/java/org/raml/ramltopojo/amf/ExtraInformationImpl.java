package org.raml.ramltopojo.amf;

import amf.client.model.domain.*;
import org.raml.ramltopojo.GenerationException;
import org.raml.ramltopojo.ScalarTypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class ExtraInformationImpl {

    public static void createInformation(AnyShape shape) {
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

        node.addProperty("oldid", ScalarTypes.stringNode(shape.id()));

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
            nodeShape.inherits().stream()
                    .filter(s -> !(s instanceof UnionShape)) // make sure it refers to a type:  todo this is very specific.
                    .map(ExtraInformationImpl::nodeToId)
                    .filter(Objects::nonNull)
                    .forEach(i -> arrayNode.addMember(ScalarTypes.stringNode(i)) );
        }

        node.addProperty("supertypes", arrayNode);
    }

    static private String nodeToId(Shape nodeShape) {

        if ( nodeShape.isLink() ) {
            return nodeShape.linkTarget()
                    .filter(ne-> ne instanceof NodeShape)
                    .map(ne -> (NodeShape) ne)
                    .map(AnyShape::id).orElseThrow(() -> new GenerationException("unable to get the id of parent type with id " + nodeShape.linkTarget().get().id()));
        }
        return nodeShape.id();
    }


    public static String oldId(Shape shape) {

        return shape.customDomainProperties().stream()
                .filter(x -> x.name().is("ramltopojo"))
                .findFirst()
                .map(x -> (ScalarNode)((ObjectNode)x.extension()).getProperty("oldid").orElse(ScalarTypes.stringNode(""))).orElse(ScalarTypes.stringNode("")).value().value();
    }


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

}
