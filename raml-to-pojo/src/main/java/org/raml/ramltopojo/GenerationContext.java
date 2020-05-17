package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.AnyShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.extensions.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface GenerationContext {

    CreationResult findCreatedType(String typeId);
    Optional<TypeName> findTypeNameByTypeId(String ramlName);

    String defaultPackage();

    void newExpectedType(String name, CreationResult creationResult);
    void createTypes(String rootDirectory) throws IOException;

    ObjectTypeHandlerPlugin pluginsForObjects(Shape... typeDeclarations);
    EnumerationTypeHandlerPlugin pluginsForEnumerations(Shape... typeDeclarations);
    UnionTypeHandlerPlugin pluginsForUnions(Shape... typeDeclarations);
    ArrayTypeHandlerPlugin pluginsForArrays(Shape... typeDeclarations);
    ReferenceTypeHandlerPlugin pluginsForReferences(Shape... typeDeclarations);

    void setupTypeHierarchy(String actualName, AnyShape typeDeclaration);
    Document api();
    Set<AnyShape> childClasses(String typeId);
    ClassName buildDefaultClassName(String name, EventType eventType);

    void createSupportTypes(String rootDirectory) throws IOException;

    TypeName createSupportClass(TypeSpec.Builder newSupportType);

    ShapeTool shapeTool();
}
