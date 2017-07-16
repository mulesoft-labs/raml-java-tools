package org.raml.builder;

import com.sun.xml.internal.bind.v2.model.impl.ModelBuilder;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.yagi.framework.model.*;
import org.raml.yagi.framework.nodes.*;

/**
 * Created. There, you have it.
 */
public class ResourceBuilder implements NodeBuilder {

    static final ModelBindingConfiguration binding = new DefaultModelBindingConfiguration();
    private String name;
    private NodeBuilder[] builders;

    public ResourceBuilder(String name) {

        this.name = name;
    }

    static  public ResourceBuilder resource(String name) {

        return new ResourceBuilder(name);

    }

    public ResourceBuilder with( NodeBuilder... builders) {

        this.builders = builders;
        return this;
    }

    @Override
    public Node buildNode() {
        ObjectNode value = new ObjectNodeImpl();
        for (NodeBuilder builder : builders) {
            value.addChild(builder.buildNode());
        }

        KeyValueNode kvn = new KeyValueNodeImpl(new StringNodeImpl(name), value);
        return kvn;
    }

    public Resource build() {


        NodeModelFactory fac = binding.bindingOf(Resource.class);
        NodeModel model = fac.create(buildNode());
        return ModelProxyBuilder.createModel(Resource.class, model, binding);
    }
}
