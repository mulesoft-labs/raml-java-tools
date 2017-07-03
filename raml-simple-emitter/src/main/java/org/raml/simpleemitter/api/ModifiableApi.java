package org.raml.simpleemitter.api;

import org.raml.parsertools.ExtensionFactory;
import org.raml.simpleemitter.ApiAugmentationFactory;
import org.raml.simpleemitter.Visitable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
@ExtensionFactory(factory = ApiAugmentationFactory.class)
public interface ModifiableApi extends Api, Visitable {

    List<Resource> resources();

    @Override
    List<TypeDeclaration> types();
}
