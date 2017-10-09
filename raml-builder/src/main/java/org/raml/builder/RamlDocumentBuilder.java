package org.raml.builder;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.model.DefaultModelElement;
import org.raml.v2.internal.impl.commons.model.StringType;
import org.raml.v2.internal.impl.commons.model.factory.TypeDeclarationModelFactory;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.yagi.framework.model.*;
import org.raml.yagi.framework.nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.raml.v2.api.RamlModelBuilder.MODEL_PACKAGE;

/**
 * Created. There, you have it.
 */
public class RamlDocumentBuilder implements NodeBuilder {

    private static final ModelBindingConfiguration binding = createV10Binding();
    private List<NodeBuilder> builders = new ArrayList<>();

    private List<AnnotationTypeBuilder> annotationTypeBuilders = new ArrayList<>();
    private List<TypeDeclarationBuilder> typeDeclarationBuilders = new ArrayList<>();
    private List<ResourceBuilder> resourceBuilders = new ArrayList<>();
    private String baseUri;
    private String title;
    private String version;
    private String mediaType;


    RamlDocumentBuilder() {

    }

    @Override
    public Node buildNode() {

        Node documentNode = new RamlDocumentNode();
        for (NodeBuilder builder : builders) {
            documentNode.addChild(builder.buildNode());
        }

        KeyValueNode baseUriNode = new KeyValueNodeImpl(new StringNodeImpl("baseUri"), new StringNodeImpl(baseUri));
        documentNode.addChild(baseUriNode);

        if( title != null ) {
            KeyValueNode titleNode = new KeyValueNodeImpl(new StringNodeImpl("title"), new StringNodeImpl(title));
            documentNode.addChild(titleNode);
        }

        KeyValueNode version = new KeyValueNodeImpl(new StringNodeImpl("version"), new StringNodeImpl(this.version));
        documentNode.addChild(version);

        if ( mediaType != null ) {
            KeyValueNode mediaType = new KeyValueNodeImpl(new StringNodeImpl("mediaType"), new StringNodeImpl(this.mediaType));
            documentNode.addChild(mediaType);
        }

        ObjectNodeImpl annotationTypeNode = new ObjectNodeImpl();
        KeyValueNodeImpl atKvn = new KeyValueNodeImpl(new StringNodeImpl("annotationTypes"), annotationTypeNode);
        documentNode.addChild(atKvn);

        for (AnnotationTypeBuilder annotationTypeBuilder : annotationTypeBuilders) {

            annotationTypeNode.addChild(annotationTypeBuilder.buildNode());
        }

        ObjectNodeImpl typesNode = new ObjectNodeImpl();
        KeyValueNodeImpl typesKvn = new KeyValueNodeImpl(new StringNodeImpl("types"), typesNode);
        documentNode.addChild(typesKvn);

        for (TypeDeclarationBuilder typeDeclarationBuilder : typeDeclarationBuilders) {

            typesNode.addChild(typeDeclarationBuilder.buildNode());
        }

        for (ResourceBuilder resourceBuilder : resourceBuilders) {

            documentNode.addChild(resourceBuilder.buildNode());
        }


        return documentNode;
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

    public RamlDocumentBuilder withResources(ResourceBuilder... resourceBuilders) {
        this.resourceBuilders.addAll(Arrays.asList(resourceBuilders));
        return this;
    }

    public Api buildModel() {

        NodeModelFactory fac = binding.bindingOf(Api.class);
        Node node = buildNode();
        NodeModel model = fac.create(node);
        return  ModelProxyBuilder.createModel(Api.class, model, binding);
    }

    static private ModelBindingConfiguration createV10Binding()
    {
        final DefaultModelBindingConfiguration bindingConfiguration = new DefaultModelBindingConfiguration();
        bindingConfiguration.bindPackage(MODEL_PACKAGE);
        // Bind all StringTypes to the StringType implementation they are only marker interfaces
        bindingConfiguration.bind(org.raml.v2.api.model.v10.system.types.StringType.class, StringType.class);
        bindingConfiguration.bind(org.raml.v2.api.model.v10.system.types.ValueType.class, StringType.class);
        bindingConfiguration.defaultTo(DefaultModelElement.class);
        bindingConfiguration.bind(TypeDeclaration.class, new TypeDeclarationModelFactory());
        bindingConfiguration.reverseBindPackage("org.raml.v2.api.model.v10.datamodel");
        return bindingConfiguration;
    }

    public static RamlDocumentBuilder document() {

        return new RamlDocumentBuilder();
    }

    public RamlDocumentBuilder baseUri(String baseUri) {

        this.baseUri = baseUri;
        return this;
    }

    public RamlDocumentBuilder title(String title) {

        this.title = title;
        return this;
    }

    public RamlDocumentBuilder version(String version) {
        this.version = version;
        return this;
    }

    public RamlDocumentBuilder mediaType(String mediaType) {

        this.mediaType = mediaType;
        return this;
    }
}
