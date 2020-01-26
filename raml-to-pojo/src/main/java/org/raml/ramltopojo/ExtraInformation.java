package org.raml.ramltopojo;

import amf.client.model.domain.ArrayNode;
import amf.client.model.domain.ObjectNode;
import amf.client.model.domain.ScalarNode;
import amf.client.model.domain.Shape;

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

    public static List<String> parentType(Shape shape) {

        return shape.customDomainProperties().stream()
                .filter(x -> x.name().is("ramltopojo"))
                .findFirst()
                .map(x -> (ArrayNode)((ObjectNode)x.extension()).getProperty("supertypes").orElse(new ArrayNode()))
                .orElse(new ArrayNode()).members().stream().map(ScalarNode.class::cast).map(x -> x.value().value()).collect(Collectors.toList());
    }
}
