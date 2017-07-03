package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;

/**
 * Created. There, you have it.
 */
public class VisitableMethod implements Visitable {
    final private Method method;

    public VisitableMethod(Method method) {
        this.method = method;
    }

    @Override
    public void visit(ApiVisitor v) {

        v.visit(method);

        for (Response response : method.responses()) {

            Visitable augResource = Augmenter.augment(Visitable.class, response);
            augResource.visit(v);
        }

    }
}
