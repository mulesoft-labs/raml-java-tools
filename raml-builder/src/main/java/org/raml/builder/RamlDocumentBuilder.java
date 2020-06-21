package org.raml.builder;

import amf.client.model.document.Document;
import amf.client.model.domain.WebApi;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class RamlDocumentBuilder implements ModelBuilder<Document> {

    private List<NodeBuilder> builders = new ArrayList<>();

   // private KeyValueNodeBuilderMap<KeyValueNodeBuilder> annotationTypeBuilders = KeyValueNodeBuilderMap.createMap();
    private List<DeclaredShapeBuilder> typeDeclarationBuilders = new ArrayList<>();
    private List<ResourceBuilder> resourceBuilders = new ArrayList();
    private String baseUri;
    private String title;
    private String version;
    private String mediaType;


    RamlDocumentBuilder() {

    }

    @Override
    public Document buildModel() {
        return buildNode();
    }

    public Document buildNode() {

        Document doc = new Document();
        WebApi apiNode = new WebApi();

       // Optional.ofNullable(baseUri).ifPresent(apiNode::withServer);
        Optional.ofNullable(version).ifPresent(apiNode::withVersion);
      //  Optional.ofNullable(title).ifPresent(apiNode::withDocumentationTitle);
        Optional.ofNullable(mediaType).ifPresent(c -> apiNode.withContentType(Collections.singletonList(c)));
        Optional.ofNullable(mediaType).ifPresent(c -> apiNode.withAccepts(Collections.singletonList(c)));
      //  Optional.ofNullable(baseUri).ifPresent(apiNode::withServer);
        apiNode.withEndPoints(resourceBuilders.stream().map(ResourceBuilder::buildNode).collect(Collectors.toList()));
        doc.withEncodes(apiNode);

        //annotationTypeBuilders.addAllToNamedNode("annotationTypes", apiNode);
        doc.withDeclares(typeDeclarationBuilders.stream().map(DeclaredShapeBuilder::buildNode).collect(Collectors.toList()));
        return doc;
    }

    public RamlDocumentBuilder with(NodeBuilder... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return this;
    }

    public RamlDocumentBuilder withAnnotationTypes(AnnotationTypeBuilder... annotationTypeBuilders) {
    //    this.annotationTypeBuilders.addAll(annotationTypeBuilders);
        return this;
    }

    public RamlDocumentBuilder withTypes(DeclaredShapeBuilder... typeBuilders) {
        this.typeDeclarationBuilders.addAll(Arrays.asList(typeBuilders));
        return this;
    }

    public RamlDocumentBuilder withResources(ResourceBuilder... resourceBuilders) {
        this.resourceBuilders.addAll(Arrays.asList(resourceBuilders));
        return this;
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
