package org.raml.builder;

import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class FacetBuilder extends KeyValueNodeBuilder<FacetBuilder> {

    private String string;
    private long number;

    public FacetBuilder(String name) {
        super(name);
    }

    public static FacetBuilder facet(String name) {

        return new FacetBuilder(name);
    }

    public FacetBuilder value(String value) {

        this.string = value;
        return this;
    }

    public FacetBuilder value(int value) {

        this.number = value;
        return this;
    }

    public FacetBuilder value(long value) {

        this.number = value;
        return this;
    }

    @Override
    protected Node createValueNode() {

        if ( string != null ) {

            return new StringNodeImpl(string);
        } else {
            return new NumberNode(number);
        }
    }
}
