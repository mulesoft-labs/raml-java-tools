package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;

/**
 * Created. There, you have it.
 */
public class ResourceBuilder extends KeyValueNodeBuilder<ResourceBuilder> implements NodeBuilder {

    private String displayName;
    private String description;

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
}
