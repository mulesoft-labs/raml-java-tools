package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;

/**
 * Created. There, you have it.
 */
public class FacetBuilder extends KeyValueNodeBuilder<FacetBuilder> {

    private ValueNodeFactory value;

    public FacetBuilder(String name) {
        super(name);
    }

    public static FacetBuilder facet(String name) {

        return new FacetBuilder(name);
    }

    public FacetBuilder ofType(String typeName) {

        this.value = ValueNodeFactories.create(typeName);
        return this;
    }

    @Override
    public KeyValueNode buildNode() {
        KeyValueNode node = super.buildNode();
        node.setValue(value.createNode());
        return node;
    }

}
