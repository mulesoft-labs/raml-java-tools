package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;

import java.util.Arrays;

/**
 * Created. There, you have it.
 */
public class ResourceBuilder extends KeyValueNodeBuilder<ResourceBuilder> implements NodeBuilder {

    private String displayName;
    private String description;
    private String relativeUri;

    private KeyValueNodeBuilderMap<ResourceBuilder> resourceBuilders = KeyValueNodeBuilderMap.createMap();
    private KeyValueNodeBuilderMap<MethodBuilder> methodBuilders = KeyValueNodeBuilderMap.createMap();

    private ResourceBuilder(String name) {
        super(name);
    }

    static public ResourceBuilder resource(String name) {

        return new ResourceBuilder(name);
    }

    @Override
    public KeyValueNode buildNode() {

        KeyValueNode resourceNode = super.buildNode();

        addProperty(resourceNode.getValue(), "displayName", displayName);
        addProperty(resourceNode.getValue(), "description", description);
        addProperty(resourceNode.getValue(), "relativeUri", relativeUri);

        resourceBuilders.addToParent(resourceNode.getValue());
        methodBuilders.addToParent(resourceNode.getValue());

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

    public ResourceBuilder withMethods(MethodBuilder... methodBuilders) {
        this.methodBuilders.addAll(Arrays.asList(methodBuilders));
        return this;
    }

}
