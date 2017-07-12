package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ReferenceNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public class ReferenceNodeHandler extends NodeHandler<ReferenceNode> {


    private final HandlerList handlerList;

    public ReferenceNodeHandler(HandlerList handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof ReferenceNode;
    }

    @Override
    public boolean handleSafely(ReferenceNode node, YamlEmitter emitter) throws IOException {


        emitter.writeTag(node.getRefName());
        return true;
    }

    private String isScalar(Node node) {

        if ( node instanceof SimpleTypeNode) {

            return ((SimpleTypeNode<?>)node).getLiteralValue();
        } else {

            return null;
        }
    }

}
