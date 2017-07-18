package org.raml.builder;

import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.yagi.framework.model.ModelProxyBuilder;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.model.NodeModelFactory;
import org.raml.yagi.framework.nodes.*;

/**
 * Created. There, you have it.
 */
public class MethodBuilder extends KeyValueNodeBuilder<Method, MethodBuilder>  {

    private ObjectNodeImpl responses = new ObjectNodeImpl();

    private MethodBuilder(String name) {
        super(name);
    }

    static public MethodBuilder method(String name) {

        return new MethodBuilder(name);
    }

    public MethodBuilder withResponse(ResponseBuilder response) {

        responses.addChild(response.buildNode());
        return this;
    }


    @Override
    public KeyValueNode buildNode() {
        KeyValueNode node =  super.buildNode();
        KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("responses"), responses);
        node.getValue().addChild(kvn);
        return node;
    }

    public Method build() {

        return super.build(Method.class, buildNode());
    }
}
