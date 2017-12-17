package org.raml.ramltopojo;

import org.raml.ramltopojo.object.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.IOException;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface GenerationContext {

    CreationResult findCreatedType(String typeName, TypeDeclaration ramlType);
    String defaultPackage();

    void newExpectedType(String name, CreationResult creationResult);

    void createTypes(String rootDirectory) throws IOException;
    ObjectTypeHandlerPlugin pluginsForObjects(TypeDeclaration... typeDeclarations);
    Api api();

    Set<String> childClasses(String ramlTypeName);

}
