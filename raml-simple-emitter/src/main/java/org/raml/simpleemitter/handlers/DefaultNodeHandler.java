package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.Node;

import java.util.Arrays;

/**
 * Created. There, you have it.
 */
public class DefaultNodeHandler extends NodeHandler<Node> {


    @Override
    public boolean handles(Node node) {
        return true;
    }

    @Override
    public void handleSafely(Node node, YamlEmitter emitter) {

        System.err.println("not handled: " + node.getClass() + ", " + Arrays.asList(node.getClass().getInterfaces()));
    }
}
