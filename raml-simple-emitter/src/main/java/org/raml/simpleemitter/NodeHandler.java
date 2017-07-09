package org.raml.simpleemitter;

import org.raml.yagi.framework.nodes.Node;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
abstract public class NodeHandler<K extends Node> {

    public abstract boolean handles(Node node);

    public boolean handle(Node node, YamlEmitter emitter) {

        try {
            K safeNode = (K) node;
            return handleSafely(safeNode, emitter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract boolean handleSafely(K node, YamlEmitter emitter) throws IOException;
}
