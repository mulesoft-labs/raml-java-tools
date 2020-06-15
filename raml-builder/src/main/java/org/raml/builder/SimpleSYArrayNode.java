package org.raml.builder;




import javax.annotation.Nonnull;

/**
 * Created. There, you have it.
 */
public class SimpleSYArrayNode extends SYArrayNode implements ArrayNode {

    public SimpleSYArrayNode() {
        super(null, null, null);
    }

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
