package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

/**
 * Created. There, you have it.
 */
public class ObjectNodeHandler extends NodeHandler<ObjectNode> {


    private final HandlerList handlerList;

    public ObjectNodeHandler(HandlerList handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof ObjectNode;
    }

    @Override
    public void handleSafely(ObjectNode node, YamlEmitter emitter) {


        String scalar = isScalar(node.getChildren().get(0));
        if ( scalar != null ) {

            System.err.println(": " + scalar);
        } else {

            handlerList.handle(node.getChildren().get(0), emitter);
        }

    }

    private String isScalar(Node node) {

        if ( node instanceof SimpleTypeNode) {

            return ((SimpleTypeNode<?>)node).getLiteralValue();
        } else {

            return null;
        }
    }

}
