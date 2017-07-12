package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
public class KeyValueNodeHandler extends NodeHandler<KeyValueNode> {


    private final HandlerList handlerList;

    public KeyValueNodeHandler(HandlerList handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof KeyValueNode;
    }

    @Override
    public boolean handleSafely(KeyValueNode node, YamlEmitter emitter) throws IOException {

        String scalar = isScalar(node.getValue());
        if ( scalar != null ) {

            emitter.writeOneLine(node.getKey().toString());
            handlerList.handle(node.getValue(), emitter);

        } else {

            emitter.writeTag(node.getKey().toString());
            handlerList.handle(node.getValue(), emitter.indent());
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
