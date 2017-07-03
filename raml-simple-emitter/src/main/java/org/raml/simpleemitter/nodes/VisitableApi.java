package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created. There, you have it.
 */
public class VisitableApi implements Visitable {
    final private Api api;

    public VisitableApi(Api api) {
        this.api = api;
    }

    @Override
    public void visit(ApiVisitor v) {

        v.visit(api);
        for (Resource resource : api.resources()) {

            Visitable augResource = Augmenter.augment(Visitable.class, resource);
            augResource.visit(v);
        }

        for (TypeDeclaration typeDeclaration : api.types()) {

            v.visit(typeDeclaration);
        }
    }
}
