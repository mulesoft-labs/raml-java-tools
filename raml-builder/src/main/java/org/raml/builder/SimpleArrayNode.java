package org.raml.builder;

import org.raml.yagi.framework.nodes.*;

import javax.annotation.Nonnull;

/**
 * Created. There, you have it.
 */
public class SimpleArrayNode extends BaseNode implements ArrayNode {

    @Override
    public boolean isJsonStyle() {
        return false;
    }

    @Nonnull
    @Override
    public Node copy() {
        return this;
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
        return NodeType.Array;
    }
}
