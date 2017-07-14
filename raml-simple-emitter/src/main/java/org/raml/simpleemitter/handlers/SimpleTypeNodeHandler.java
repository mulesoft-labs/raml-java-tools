package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public class SimpleTypeNodeHandler extends NodeHandler<SimpleTypeNode<?>> {


    public SimpleTypeNodeHandler() {
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof SimpleTypeNode;
    }

    @Override
    public boolean handleSafely(SimpleTypeNode<?> node, YamlEmitter emitter) throws IOException {

        emitter.writeValue(node);
        return true;
    }
}
