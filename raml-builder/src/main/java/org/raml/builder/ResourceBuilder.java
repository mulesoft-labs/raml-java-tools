package org.raml.builder;

import amf.client.model.domain.EndPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class ResourceBuilder extends DomainElementBuilder<EndPoint, ResourceBuilder> {

    private final String name;
    private String displayName;
    private String description;
    private String relativeUri;

    private List<ResourceBuilder> resourceBuilders = new ArrayList<>();
    private List<OperationBuilder> methodBuilders = new ArrayList<>();

    private ResourceBuilder(String name) {
        super();
        this.name = name;
    }

    static public ResourceBuilder resource(String name) {

        return new ResourceBuilder(name);
    }

    @Override
    public EndPoint buildNodeLocally() {

        EndPoint resourceNode = new EndPoint();

        Optional.ofNullable(description).ifPresent(resourceNode::withDescription);
        //Optional.ofNullable(relativeUri).ifPresent(resourceNode::withPath);
        Optional.ofNullable(name).ifPresent(resourceNode::withName);
        Optional.ofNullable(name).ifPresent(resourceNode::withPath);

        resourceNode.withOperations(methodBuilders.stream().map(OperationBuilder::buildNodeLocally).collect(Collectors.toList()));

        return resourceNode;
    }

    public ResourceBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ResourceBuilder description(String comment) {

        this.description = comment;
        return this;
    }

    public ResourceBuilder relativeUri(String relativeUri) {
        this.relativeUri = relativeUri;

        return this;
    }

    public ResourceBuilder withResources(ResourceBuilder... resourceBuilders) {
        this.resourceBuilders.addAll(Arrays.asList(resourceBuilders));
        return this;
    }

    public ResourceBuilder withMethods(OperationBuilder... operationBuilders) {
        this.methodBuilders.addAll(Arrays.asList(operationBuilders));
        return this;
    }

}
