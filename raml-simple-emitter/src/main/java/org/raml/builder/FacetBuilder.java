package org.raml.builder;

import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class FacetBuilder extends KeyValueNodeBuilder<FacetBuilder> {

    private String value;

    public FacetBuilder(String name) {
        super(name);
    }

    public static FacetBuilder facet(String name) {

        return new FacetBuilder(name);
    }

    public FacetBuilder value(String name) {

        this.value = name;
        return this;
    }

    @Override
    protected Node createValueNode() {
        return new StringNodeImpl(value);
    }
}
