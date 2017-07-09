package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.Node;

/**
 * Created. There, you have it.
 */
public class TypeDeclarationNodeHandler extends NodeHandler<TypeDeclarationNode> {


    private final HandlerList handlerList;

    public TypeDeclarationNodeHandler(HandlerList handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof TypeDeclarationNode;
    }

    @Override
    public boolean handleSafely(TypeDeclarationNode node, YamlEmitter emitter) {


        for (Node child : node.getChildren()) {

            handlerList.handle(child, emitter);
        }

        return true;
    }

}
