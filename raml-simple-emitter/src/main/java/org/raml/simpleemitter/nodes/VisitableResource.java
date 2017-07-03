package org.raml.simpleemitter.nodes;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.simpleemitter.api.ModifiableMethod;
import org.raml.simpleemitter.api.ModifiableResource;
import org.raml.simpleemitter.api.ModifiableResponse;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class VisitableResource extends Helper implements Visitable {
    final private Resource resource;

    public VisitableResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void visit(ApiVisitor v) {

        v.visit(Augmenter.augment(ModifiableResource.class, resource));
    }

    public List<Resource> resources() {

        return toModifiable(resource.resources(), ModifiableResource.class);
    }

    public List<Method> methods() {

        return toModifiable(resource.methods(), ModifiableMethod.class);
    }

}
