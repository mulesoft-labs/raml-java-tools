package org.raml.simpleemitter.api;

import org.omg.CORBA.Object;
import org.raml.parsertools.ExtensionFactory;
import org.raml.simpleemitter.ApiAugmentationFactory;
import org.raml.simpleemitter.Visitable;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;


/**
 * Created. There, you have it.
 */
@ExtensionFactory(factory = ApiAugmentationFactory.class)
public interface ModifiableObjectTypeDeclaration extends Visitable, ObjectTypeDeclaration {

    @Override
    List<TypeDeclaration> properties();
}
