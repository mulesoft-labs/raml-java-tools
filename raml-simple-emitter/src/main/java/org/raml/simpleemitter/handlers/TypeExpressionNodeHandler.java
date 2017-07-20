package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryRefNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class TypeExpressionNodeHandler extends SubclassedNodeHandler<TypeExpressionNode> {

    public TypeExpressionNodeHandler(HandlerList handlerList) {
        super(TypeExpressionNode.class, new HandlerList(Collections.<NodeHandler<? extends Node>>singletonList(new TypeDeclarationNodeHandler(handlerList))));
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof TypeExpressionNode;
    }

    @Override
    public boolean handleSafely(TypeExpressionNode node, YamlEmitter emitter) throws IOException {

        List<LibraryRefNode> descs = node.findDescendantsWith(LibraryRefNode.class);
        if ( descs.size() != 0 && descs.get(0) != null ) {

            emitter.writeObjectValue(descs.get(0).getRefName() + "." +node.getTypeExpressionText());
        } else {

            emitter.writeObjectValue(node.getTypeExpressionText());
        }
        return true;
    }
}
