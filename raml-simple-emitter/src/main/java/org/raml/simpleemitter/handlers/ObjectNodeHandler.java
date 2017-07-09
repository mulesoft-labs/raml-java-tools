package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import java.util.Collections;

/**
 * Created. There, you have it.
 */
public class ObjectNodeHandler extends NodeHandler<ObjectNode> {


    private final HandlerList handlerList;
    private final HandlerList subclassHandlerList;


    public ObjectNodeHandler(HandlerList handlerList) {

        this.handlerList = handlerList;
        this.subclassHandlerList = new HandlerList(Collections.<NodeHandler<? extends Node>>singletonList(new TypeDeclarationNodeHandler(handlerList)));
    }

    @Override
    public boolean handles(Node node) {


        if ( subclassHandlerList.handles(node) ) {
            return true;
        } else {

            return node instanceof ObjectNode;
        }
    }

    @Override
    public boolean handle(Node node, YamlEmitter emitter) {

        if ( ! subclassHandlerList.handle(node, emitter) ) {
            return true;
        } else {

            return handleSafely((ObjectNode) node, emitter);
        }
    }

    @Override
    public boolean handleSafely(ObjectNode node, YamlEmitter emitter) {

        for (Node child : node.getChildren()) {

            String scalar = isScalar(node.getChildren().get(0));
            if ( scalar != null ) {

                System.err.println(": " + scalar);
            } else {

                handlerList.handle(child, emitter);
            }
        }

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
