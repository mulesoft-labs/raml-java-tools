package org.raml.simpleemitter;

import org.raml.parsertools.Augmenter;
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
    public void emit(final Writer w) throws IOException {

        RamlDocumentNode n = (RamlDocumentNode) nodeModel.getNode();
        w.write("#%RAML 1.0\n");

        for (Node node : n.getChildren()) {

            VisitableNode v = Augmenter.augment(VisitableNode.class, node);
            v.visit(new NodeVisitor() {
                @Override
                public void visit(KeyValueNode node) {
                    try {
                        w.write(node.getKey().toString() + ":\n");
                        w.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        w.flush();
    }
}
