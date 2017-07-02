package org.raml.simpleemitter;

import org.raml.parsertools.ExtensionFactory;
import org.raml.yagi.framework.nodes.KeyValueNode;

/**
 * Created. There, you have it.
 */
public interface NodeVisitor {

    void visit(KeyValueNode node);
}
