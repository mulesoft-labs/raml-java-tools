package org.raml.builder;

import org.raml.v2.internal.impl.commons.nodes.ResourceNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

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

        KeyValueNode resourceNode = new ResourceNode();
        resourceNode.addChild(new StringNodeImpl("/foo"));
        resourceNode.addChild(new ObjectNodeImpl());

        KeyValueNode baseUriNode = new KeyValueNodeImpl(new StringNodeImpl("displayName"), new StringNodeImpl(displayName));
        resourceNode.getValue().addChild(baseUriNode);

        KeyValueNode description = new KeyValueNodeImpl(new StringNodeImpl("description"), new StringNodeImpl(this.description));
        resourceNode.getValue().addChild(description);

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
