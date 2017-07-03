package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.simpleemitter.api.ModifiableMethod;
import org.raml.simpleemitter.api.ModifiableResource;
import org.raml.simpleemitter.api.ModifiableResponse;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class VisitableMethod extends Helper implements Visitable {
    final private Method method;

    public VisitableMethod(Method method) {
        this.method = method;
    }

    @Override
    public void visit(ApiVisitor v) {

        v.visit(Augmenter.augment(ModifiableMethod.class, method));
    }

    public List<Response> responses() {

        return toModifiable(method.responses(), ModifiableResponse.class);
    }
}
