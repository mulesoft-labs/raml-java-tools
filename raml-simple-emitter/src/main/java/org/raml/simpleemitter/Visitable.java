package org.raml.simpleemitter;


import org.raml.parsertools.ExtensionFactory;

/**
 * Created. There, you have it.
 */
@ExtensionFactory(factory = ApiAugmentationFactory.class)
public interface Visitable {

    void visit(ApiVisitor v);
}
