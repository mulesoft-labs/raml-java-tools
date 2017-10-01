package org.raml.builder;

import org.raml.yagi.framework.nodes.*;

import javax.annotation.Nonnull;

/**
 * Created. There, you have it.
 */
public class NumberNode extends BaseNode implements IntegerNode  {

    private Long number;

    public NumberNode(long number) {
        this.number = number;
    }

    @Nonnull
    @Override
    public Node copy() {
        return this;
    }


    @Override
    public Long getValue() {
        return number;
    }

    @Override
    public String getLiteralValue() {
        return Long.toString(number);
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
