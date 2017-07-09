package org.raml.simpleemitter.handlers;

import com.google.common.base.Joiner;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class NullNodeHandler extends NodeHandler<NullNode> {


    private final HandlerList handlerList;

    public NullNodeHandler(HandlerList handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof NullNode;
    }

    @Override
    public void handleSafely(NullNode node, YamlEmitter emitter) throws IOException {


        emitter.write("value: null");
    }

    private String isScalar(Node node) {

        if ( node instanceof SimpleTypeNode) {

            return ((SimpleTypeNode<?>)node).getLiteralValue();
        } else {

            return null;
        }
    }

}
