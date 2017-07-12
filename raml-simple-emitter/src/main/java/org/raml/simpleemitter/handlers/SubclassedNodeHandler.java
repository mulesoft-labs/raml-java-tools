package org.raml.simpleemitter.handlers;

import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;

import java.io.IOException;

/**
 * Created. There, you have it.
 */
abstract public class SubclassedNodeHandler<T extends Node> extends NodeHandler<T> {

    private final Class<?> superClass;
    private final HandlerList subclassHandlerList;

    public SubclassedNodeHandler(Class<?> superClass, HandlerList subclassHandlerList) {
        this.superClass = superClass;
        this.subclassHandlerList = subclassHandlerList;
    }

    @Override
    public boolean handles(Node node) {

        return subclassHandlerList.handles(node) || superClass.isInstance(node);
    }

    @Override
    public boolean handle(Node node, YamlEmitter emitter)  {
        try {
            return subclassHandlerList.handle(node, emitter) || handleSafely((T) node, emitter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
