package org.raml.simpleemitter;

import org.raml.parsertools.AugmentationExtensionFactory;
import org.raml.simpleemitter.nodes.*;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Arrays;


/**
 * Created. There, you have it.
 */
public class ApiAugmentationFactory implements AugmentationExtensionFactory {

    @Override
    public Object create(Object object) {
        throw new IllegalArgumentException(Arrays.asList(object.getClass().getInterfaces()) + " not handled");
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

    public VisitableObjectTypeDeclaration create(final ObjectTypeDeclaration declaration) {

        return new VisitableObjectTypeDeclaration(declaration);
    }

    public VisitableStringTypeDeclaration create(final StringTypeDeclaration declaration) {

        return new VisitableStringTypeDeclaration(declaration);
    }

    public VisitableIntegerTypeDeclaration create(final IntegerTypeDeclaration declaration) {

        return new VisitableIntegerTypeDeclaration(declaration);
    }

}
