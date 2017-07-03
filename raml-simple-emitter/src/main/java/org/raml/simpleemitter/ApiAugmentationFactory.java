package org.raml.simpleemitter;

import org.raml.parsertools.AugmentationExtensionFactory;
import org.raml.simpleemitter.nodes.VisitableApi;
import org.raml.simpleemitter.nodes.VisitableMethod;
import org.raml.simpleemitter.nodes.VisitableResource;
import org.raml.simpleemitter.nodes.VisitableResponse;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;


/**
 * Created. There, you have it.
 */
public class ApiAugmentationFactory implements AugmentationExtensionFactory {

    @Override
    public Object create(Object object) {
        throw new IllegalArgumentException(object.getClass() + " not handled");
    }

    public VisitableApi create(final Api api) {

        return new VisitableApi(api);
    }

    public VisitableResource create(final Resource resource) {

        return new VisitableResource(resource);
    }


    public VisitableMethod create(final Method resource) {

        return new VisitableMethod(resource);
    }

    public VisitableResponse create(final Response response) {

        return new VisitableResponse(response);
    }

}
