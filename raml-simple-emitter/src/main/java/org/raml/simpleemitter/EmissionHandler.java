package org.raml.simpleemitter;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;

import java.io.IOException;
import java.io.Writer;

/**
 * Created. There, you have it.
 */
public class EmissionHandler implements RamlEmitter {

    private final NodeModel nodeModel;
    private Api delegate;

    public EmissionHandler(Api delegate) {

        this.delegate = delegate;
        this.nodeModel = (NodeModel) delegate;
    }

    @Override
    public void emit(Writer w) throws IOException {

        RamlDocumentNode n = (RamlDocumentNode) nodeModel.getNode();
        w.write("#%RAML 1.0\n");
        for (Node node : n.getChildren()) {
            KeyValueNode kvn = (KeyValueNode) node;
            w.write(kvn.getKey().toString() + ":");
            w.write(kvn.getValue().toString() + "\n");
        }

        w.flush();
    }
}
