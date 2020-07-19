package org.raml.builder;


import amf.client.model.domain.WebApi;
import amf.client.validate.ValidationReport;
import amf.core.AMF;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class RamlDocumentBuilder implements ModelBuilder<WebApiDocument> {

    static {
        AMF.init();
    }

    private List<NodeBuilder> builders = new ArrayList<>();

    // private KeyValueNodeBuilderMap<KeyValueNodeBuilder> annotationTypeBuilders = KeyValueNodeBuilderMap.createMap();
    private final List<DeclaredShapeBuilder<?>> typeDeclarationBuilders = new ArrayList<>();
    private final List<ResourceBuilder> resourceBuilders = new ArrayList();
    private String baseUri;
    private String title;
    private String version;
    private String mediaType;


    RamlDocumentBuilder() {


    }

    @Override
    public WebApiDocument buildModel() {

        try {
            WebApiDocument document = buildNode();
            ValidationReport s = Raml10.validate(document).get();
            if (!s.conforms()) {
                throw new ModelBuilderException(s);
            }

            return document;
        } catch (ModelBuilderException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelBuilderException(e);
        }
    }

    public WebApiDocument buildNode() {

        WebApiDocument doc = new WebApiDocument();

        WebApi apiNode = new WebApi();
        doc.withEncodes(apiNode);



        Optional.ofNullable(version).ifPresent(apiNode::withVersion);
        Optional.ofNullable(title).ifPresent(apiNode::withName);
        Optional.ofNullable(mediaType).ifPresent(c -> apiNode.withContentType(Collections.singletonList(c)));
        Optional.ofNullable(mediaType).ifPresent(c -> apiNode.withAccepts(Collections.singletonList(c)));

        // Not sure where this goes....
        // Optional.ofNullable(baseUri).ifPresent(apiNode::withServer);

        apiNode.withEndPoints(resourceBuilders.stream().map(ResourceBuilder::buildNodeLocally).collect(Collectors.toList()));

        //annotationTypeBuilders.addAllToNamedNode("annotationTypes", apiNode);
        doc.withDeclares(typeDeclarationBuilders.stream().map(DeclaredShapeBuilder::buildNodeLocally).collect(Collectors.toList()));

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

    public RamlDocumentBuilder withTypes(DeclaredShapeBuilder<?>... typeBuilders) {
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
