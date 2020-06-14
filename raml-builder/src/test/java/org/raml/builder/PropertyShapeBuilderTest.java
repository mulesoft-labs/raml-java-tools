package org.raml.builder;

import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;

/**
 * Created. There, you have it.
 */
public class PropertyShapeBuilderTest {

    @Test
    public void complexType() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.simpleType("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.simpleType("string")))
                                        .withProperty(
                                                PropertyShapeBuilder.property("address", TypeShapeBuilder.simpleType("string")).required(false))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("object", api.types().get(0).type());
        TypeDeclaration typeDeclaration = ((ObjectTypeDeclaration) api.types().get(0)).properties().get(0);
        assertEquals("name", typeDeclaration.name());
        assertEquals("string", typeDeclaration.type());
        assertEquals(true, typeDeclaration.required());
        
        TypeDeclaration typeDeclaration2 = ((ObjectTypeDeclaration) api.types().get(0)).properties().get(1);
        assertEquals("address", typeDeclaration2.name());
        assertEquals("string", typeDeclaration2.type());
        assertEquals(false, typeDeclaration2.required());
    }

    
}
