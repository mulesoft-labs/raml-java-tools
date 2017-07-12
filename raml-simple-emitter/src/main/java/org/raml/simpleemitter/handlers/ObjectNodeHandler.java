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
public class ObjectNodeHandler extends SubclassedNodeHandler<ObjectNode> {


    private final HandlerList handlerList;


    public ObjectNodeHandler(HandlerList handlerList) {

        super(ObjectNode.class, new HandlerList(Collections.<NodeHandler<? extends Node>>singletonList(new TypeDeclarationNodeHandler(handlerList))));
        this.handlerList = handlerList;
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
