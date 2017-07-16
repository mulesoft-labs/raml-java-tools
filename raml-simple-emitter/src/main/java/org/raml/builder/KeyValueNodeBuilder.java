package org.raml.builder;

import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.yagi.framework.model.*;
import org.raml.yagi.framework.nodes.*;

/**
 * Created. There, you have it.
 */
public class KeyValueNodeBuilder<T, B extends KeyValueNodeBuilder> implements NodeBuilder {
    static final ModelBindingConfiguration binding = new DefaultModelBindingConfiguration();

    private String name;
    private NodeBuilder[] builders;

    public KeyValueNodeBuilder(String name) {
        this.name = name;
    }

    public B with(NodeBuilder... builders) {

        this.builders = builders;
        return (B) this;
    }

    public Node buildNode() {
        ObjectNode value = new ObjectNodeImpl();
        for (NodeBuilder builder : builders) {
            value.addChild(builder.buildNode());
        }

        return new KeyValueNodeImpl(new StringNodeImpl(name), value);
    }

    public T build(Class<T> cls, Node node) {

        NodeModelFactory fac = binding.bindingOf(Resource.class);
        NodeModel model = fac.create(node);
        return  ModelProxyBuilder.createModel(cls, model, binding);
    }
}
