package org.raml.simpleemitter;


import org.raml.parsertools.ExtensionFactory;

/**
 * Created. There, you have it.
 */
@ExtensionFactory(factory = NodeAugmentationFactory.class)
public interface VisitableNode {

    void visit(NodeVisitor v);
}
