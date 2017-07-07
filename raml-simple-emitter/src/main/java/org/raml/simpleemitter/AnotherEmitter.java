package org.raml.simpleemitter;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.Node;

/**
 * Created. There, you have it.
 */
public class AnotherEmitter {

    private HandlerList list = new HandlerList();

    public void emit(Api api) {

        NodeModel model = (NodeModel) api;
        Node node = model.getNode();

        for (Node o : node.getChildren()) {

            list.handle(o, new YamlEmitter());
        }
    }
}
