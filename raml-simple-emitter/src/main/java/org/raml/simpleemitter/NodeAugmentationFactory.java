package org.raml.simpleemitter;

import org.raml.parsertools.AugmentationExtensionFactory;
import org.raml.simpleemitter.nodes.KeyValueVisitableNode;
import org.raml.v2.internal.impl.commons.nodes.TypesNode;
import org.raml.yagi.framework.nodes.KeyValueNode;

/**
 * Created. There, you have it.
 */
public class NodeAugmentationFactory implements AugmentationExtensionFactory {

    @Override
    public Object create(Object object) {
        throw new IllegalArgumentException(object.getClass() + " not handled");
    }

    public Object create(final KeyValueNode node) {

        return new KeyValueVisitableNode(node);
    }

    public Object create(final TypesNode node) {

        return new KeyValueVisitableNode(node);
    }

}
