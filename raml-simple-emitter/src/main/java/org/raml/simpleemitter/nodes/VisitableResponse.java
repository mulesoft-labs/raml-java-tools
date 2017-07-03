package org.raml.simpleemitter.nodes;

import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;

/**
 * Created. There, you have it.
 */
public class VisitableResponse implements Visitable {
    final private Response response;

    public VisitableResponse(Response response) {
        this.response = response;
    }

    @Override
    public void visit(ApiVisitor v) {

        v.visit(response);
    }
}
