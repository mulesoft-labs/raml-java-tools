package org.raml.simpleemitter;

import org.raml.simpleemitter.handlers.*;
import org.raml.yagi.framework.nodes.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class HandlerList {

    private final List<NodeHandler<? extends Node >> nodeList = new ArrayList<>();
    {

        nodeList.add(new TypeDeclarationNodeHandler(this));
        nodeList.add(new KeyValueNodeHandler(this));
        nodeList.add(new ObjectNodeHandler(this));
        nodeList.add(new ArrayNodeHandler(this));
        nodeList.add(new NullNodeHandler(this));
        nodeList.add(new ReferenceNodeHandler(this));
        nodeList.add(new DefaultNodeHandler());
    }

    public <T extends Node> void handle(T node, YamlEmitter emitter) {

        for (NodeHandler<?> nodeHandler : nodeList) {

            if ( nodeHandler.handles(node)) {

                nodeHandler.handle(node, emitter);
                return;
            }
        }
    }

}
