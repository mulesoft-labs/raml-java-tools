package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created. There, you have it.
 */
public class VisitableResource implements Visitable {
    final private Resource resource;

    public VisitableResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void visit(ApiVisitor v) {

        v.visit(resource);
        for (Method method : resource.methods()) {

            Visitable augResource = Augmenter.augment(Visitable.class, method);
            augResource.visit(v);
        }

        for (Resource sub : resource.resources()) {

            Visitable augResource = Augmenter.augment(Visitable.class, sub);
            augResource.visit(v);

        }
    }
}
