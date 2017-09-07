package org.raml.builder;

import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ResourceBuilder extends KeyValueNodeBuilder<Resource, ResourceBuilder> implements NodeBuilder {

    private ResourceBuilder(String name) {
        super(name);
    }

    static public ResourceBuilder resource(String name) {

        return new ResourceBuilder(name);
    }


    public Resource build() {

        return super.build(Resource.class, buildNode());
    }
}
