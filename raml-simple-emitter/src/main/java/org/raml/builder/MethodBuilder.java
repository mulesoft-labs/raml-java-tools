package org.raml.builder;

import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.yagi.framework.model.ModelProxyBuilder;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.model.NodeModelFactory;

/**
 * Created. There, you have it.
 */
public class MethodBuilder extends KeyValueNodeBuilder<Method, MethodBuilder>  {

    private MethodBuilder(String name) {
        super(name);
    }

    static public MethodBuilder method(String name) {

        return new MethodBuilder(name);
    }

    public Method build() {

        return super.build(Method.class, buildNode());
    }
}
