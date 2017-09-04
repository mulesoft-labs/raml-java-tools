package org.raml.builder;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.yagi.framework.model.*;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class RamlDocumentBuilder implements NodeBuilder {

    private static final ModelBindingConfiguration binding = new DefaultModelBindingConfiguration();
    private NodeBuilder[] builders = new NodeBuilder[0];

    private List<AnnotationTypeBuilder> annotationTypeBuilders = new ArrayList<>();

    RamlDocumentBuilder() {

    }

    @Override
    public Node buildNode() {

        Node n = new RamlDocumentNode();
        for (NodeBuilder builder : builders) {
            n.addChild(builder.buildNode());
        }

        ObjectNodeImpl valueNode = new ObjectNodeImpl();
        KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("annotationTypes"), valueNode);
        n.addChild(kvn);

        for (AnnotationTypeBuilder annotationTypeBuilder : annotationTypeBuilders) {

            valueNode.addChild(annotationTypeBuilder.buildNode());
        }

        return n;
    }

    public RamlDocumentBuilder with(NodeBuilder... builders) {

        this.builders = builders;
        return this;
    }

    public RamlDocumentBuilder withAnnotationTypes(AnnotationTypeBuilder... annotationTypeBuilders) {
        this.annotationTypeBuilders.addAll(Arrays.asList(annotationTypeBuilders));
        return this;
    }

    public Api build() {

        NodeModelFactory fac = binding.bindingOf(Api.class);
        Node node = buildNode();
        NodeModel model = fac.create(node);
        return  ModelProxyBuilder.createModel(Api.class, model, binding);
    }


    public static RamlDocumentBuilder document() {

        return new RamlDocumentBuilder();
    }
}
