package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.simpleemitter.api.ModifiableResponse;
import org.raml.simpleemitter.api.ModifiableTypeDeclaration;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class VisitableResponse extends Helper implements Visitable {
    final private Response response;

    public VisitableResponse(Response response) {
        this.response = response;
    }

    @Override
    public void visit(ApiVisitor v) {

        v.visit(Augmenter.augment(ModifiableResponse.class, response));
    }

    public List<TypeDeclaration> body() {

        return toModifiable(response.body(), ModifiableTypeDeclaration.class);
    }
}
