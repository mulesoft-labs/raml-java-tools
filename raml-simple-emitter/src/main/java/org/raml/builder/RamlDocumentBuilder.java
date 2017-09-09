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
    private List<NodeBuilder> builders = new ArrayList<>();

    private List<AnnotationTypeBuilder> annotationTypeBuilders = new ArrayList<>();
    private List<TypeDeclarationBuilder> typeDeclarationBuilders = new ArrayList<>();


    RamlDocumentBuilder() {

    }

    @Override
    public Node buildNode() {

        Node n = new RamlDocumentNode();
        for (NodeBuilder builder : builders) {
            n.addChild(builder.buildNode());
        }

        ObjectNodeImpl annotationTypeNode = new ObjectNodeImpl();
        KeyValueNodeImpl atKvn = new KeyValueNodeImpl(new StringNodeImpl("annotationTypes"), annotationTypeNode);
        n.addChild(atKvn);

        for (AnnotationTypeBuilder annotationTypeBuilder : annotationTypeBuilders) {

            annotationTypeNode.addChild(annotationTypeBuilder.buildNode());
        }

        ObjectNodeImpl typesNode = new ObjectNodeImpl();
        KeyValueNodeImpl typesKvn = new KeyValueNodeImpl(new StringNodeImpl("types"), typesNode);
        n.addChild(typesKvn);

        for (TypeDeclarationBuilder typeDeclarationBuilder : typeDeclarationBuilders) {

            typesNode.addChild(typeDeclarationBuilder.buildNode());
        }

        return n;
    }

    public RamlDocumentBuilder with(NodeBuilder... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return this;
    }

    public RamlDocumentBuilder withAnnotationTypes(AnnotationTypeBuilder... annotationTypeBuilders) {
        this.annotationTypeBuilders.addAll(Arrays.asList(annotationTypeBuilders));
        return this;
    }

    public RamlDocumentBuilder withTypes(TypeDeclarationBuilder... typeBuilders) {
        this.typeDeclarationBuilders.addAll(Arrays.asList(typeBuilders));
        return this;
    }

    public Api buildModel() {

        NodeModelFactory fac = binding.bindingOf(Api.class);
        Node node = buildNode();
        NodeModel model = fac.create(node);
        return  ModelProxyBuilder.createModel(Api.class, model, binding);
    }


    public static RamlDocumentBuilder document() {

        return new RamlDocumentBuilder();
    }
}
