package org.raml.builder;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.yagi.framework.model.*;
import org.raml.yagi.framework.nodes.Node;

/**
 * Created. There, you have it.
 */
public class RamlDocumentBuilder implements NodeBuilder {

    static final ModelBindingConfiguration binding = new DefaultModelBindingConfiguration();
    private NodeBuilder[] builders = new NodeBuilder[0];

    RamlDocumentBuilder() {

    }

    @Override
    public Node buildNode() {

        Node n = new RamlDocumentNode();
        for (NodeBuilder builder : builders) {
            n.addChild(builder.buildNode());
        }

        return n;
    }

    public RamlDocumentBuilder with(NodeBuilder... builders) {

        this.builders = builders;
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
