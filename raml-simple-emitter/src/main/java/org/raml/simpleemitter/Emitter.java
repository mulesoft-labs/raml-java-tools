package org.raml.simpleemitter;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.Node;

import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created. There, you have it.
 */
public class Emitter {

    private HandlerList list = new HandlerList();

    public void emit(Api api) {

        emit(api, new OutputStreamWriter(System.out));
    }

    public void emit(Api api, Writer w) {

        NodeModel model = (NodeModel) api;
        Node node = model.getNode();

        for (Node o : node.getChildren()) {

            list.handle(o, new YamlEmitter(w, 0));
        }
    }
}
