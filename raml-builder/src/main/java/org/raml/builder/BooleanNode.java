package org.raml.builder;

import org.raml.yagi.framework.nodes.*;

import javax.annotation.Nonnull;

/**
 * Created. There, you have it.
 */
public class BooleanNode extends BaseNode implements org.raml.yagi.framework.nodes.BooleanNode {

    private Boolean booleanValue;

    public BooleanNode(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @Nonnull
    @Override
    public Node copy() {
        return this;
    }


    @Override
    public Boolean getValue() {
        return booleanValue;
    }

    @Override
    public String getLiteralValue() {
        return Boolean.toString(booleanValue);
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Integer;
    }
}
