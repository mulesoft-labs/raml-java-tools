package org.raml.simpleemitter.handlers;

import com.google.common.base.Joiner;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ArrayNodeHandler extends NodeHandler<ArrayNode> {


    private final HandlerList handlerList;

    public ArrayNodeHandler(HandlerList handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof ArrayNode;
    }

    @Override
    public boolean handleSafely(ArrayNode node, YamlEmitter emitter) throws IOException {


        List<String> buf = new ArrayList<>();
        for (Node child : node.getChildren()) {
            String value = isScalar(child);
            if ( value != null ){

                buf.add(value);
            } else {

                buf.add("whatevs");
            }
        }
     //   emitter.write("[" + Joiner.on(",").join(buf) + "]");
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
