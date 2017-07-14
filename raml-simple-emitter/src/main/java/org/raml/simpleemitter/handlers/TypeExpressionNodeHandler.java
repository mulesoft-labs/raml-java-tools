package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;

import java.io.IOException;
import java.util.Collections;

/**
 * Created. There, you have it.
 */
public class TypeExpressionNodeHandler extends SubclassedNodeHandler<TypeExpressionNode> {


    private final HandlerList handlerList;

    public TypeExpressionNodeHandler(HandlerList handlerList) {
        super(TypeExpressionNode.class, new HandlerList(Collections.<NodeHandler<? extends Node>>singletonList(new TypeDeclarationNodeHandler(handlerList))));
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof TypeExpressionNode;
    }

    @Override
    public boolean handleSafely(TypeExpressionNode node, YamlEmitter emitter) throws IOException {

        emitter.writeObjectValue(node.getTypeExpressionText());
        return true;
    }
}
