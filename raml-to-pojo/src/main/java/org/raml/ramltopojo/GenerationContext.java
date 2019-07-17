package org.raml.ramltopojo;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.extensions.*;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import webapi.WebApiDocument;

import java.io.IOException;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface GenerationContext {

    CreationResult findCreatedType(String typeName, Shape ramlType);
    String defaultPackage();

    void newExpectedType(String name, CreationResult creationResult);
    void createTypes(String rootDirectory) throws IOException;

    ObjectTypeHandlerPlugin pluginsForObjects(Shape... typeDeclarations);
    EnumerationTypeHandlerPlugin pluginsForEnumerations(Shape... typeDeclarations);
    UnionTypeHandlerPlugin pluginsForUnions(Shape... typeDeclarations);
    ArrayTypeHandlerPlugin pluginsForArrays(Shape... typeDeclarations);
    ReferenceTypeHandlerPlugin pluginsForReferences(Shape... typeDeclarations);

    void setupTypeHierarchy(TypeDeclaration typeDeclaration);
    WebApiDocument api();
    Set<String> childClasses(String ramlTypeName);
    ClassName buildDefaultClassName(String name, EventType eventType);

    void createSupportTypes(String rootDirectory) throws IOException;

    TypeName createSupportClass(TypeSpec.Builder newSupportType);
}
