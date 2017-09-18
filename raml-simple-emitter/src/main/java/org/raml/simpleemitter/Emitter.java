package org.raml.simpleemitter;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.Node;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created. There, you have it.
 */
public class Emitter {

    final private HandlerList list;

    public Emitter(HandlerList list) {
        this.list = list;
    }

    public Emitter() {

        list = new HandlerList();
    }

    public void emit(Api api) throws IOException {

        emit(api, new OutputStreamWriter(System.out));
    }

    public void emit(Api api, Writer w) throws IOException {

        w.write("#%RAML 1.0");
        NodeModel model = (NodeModel) api;
        Node node = model.getNode();

        for (Node o : node.getChildren()) {

            list.handle(o, createEmitter(w));
        }
    }

    protected YamlEmitter createEmitter(Writer w) {
        return new YamlEmitter(w, 0);
    }
}
