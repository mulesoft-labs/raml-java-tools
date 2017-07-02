package org.raml.simpleemitter.nodes;

import org.raml.simpleemitter.NodeVisitor;
import org.raml.simpleemitter.VisitableNode;
import org.raml.yagi.framework.nodes.KeyValueNode;

/**
 * Created. There, you have it.
 */
public class KeyValueVisitableNode implements VisitableNode {
    private final KeyValueNode node;

    public KeyValueVisitableNode(KeyValueNode node) {
        this.node = node;
    }

    @Override
    public void visit(NodeVisitor v) {
        v.visit(node);
    }
}
