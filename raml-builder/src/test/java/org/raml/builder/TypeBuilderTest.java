package org.raml.builder;

import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.*;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;

/**
 * Created. There, you have it.
 */
public class TypeBuilderTest {

    @Test
    public void simpleType() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("boolean")))
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("boolean", api.types().get(0).type());
    }

    // the absolutely stupidest type ever.
    @Test
    public void enumeratedBoolean() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("boolean").enumValues(true, false)))
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals(Arrays.asList(true, false), ((BooleanTypeDeclaration)api.types().get(0)).enumValues());
    }

    @Test
    public void enumeratedInteger() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("integer").enumValues(1,2,3)))
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("integer", api.types().get(0).type());
        assertEquals(Arrays.asList(1L,2L,3L), ((IntegerTypeDeclaration)api.types().get(0)).enumValues());

    }

    @Test
    public void enumeratedString() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("string").enumValues("1", "2", "3")))
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("string", api.types().get(0).type());
        assertEquals(Arrays.asList("1","2","3"), ((StringTypeDeclaration)api.types().get(0)).enumValues());
    }

    @Test
    public void complexType() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.type("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("object", api.types().get(0).type());
        assertEquals("name", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).type());
    }

    @Test
    public void complexInheritance() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Parent")
                                .ofType(TypeShapeBuilder.type("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.type("string")))
                                ),

                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("Parent")
                                        .withProperty(
                                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.type("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(1).name());
        assertEquals("Parent", api.types().get(1).type());
        assertEquals("name", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).type());
        assertEquals("subName", ((ObjectTypeDeclaration)api.types().get(1)).properties().get(1).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(1)).properties().get(1).type());
    }

    @Test
    public void multipleInheritance() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Parent1")
                                .ofType(TypeShapeBuilder.type("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.type("string")))
                                ),
                        AnyShapeBuilder.typeDeclaration("Parent2")
                                .ofType(TypeShapeBuilder.type("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name2", TypeShapeBuilder.type("string")))
                                ),


                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("Parent1", "Parent2")
                                        .withProperty(
                                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.type("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(2).name());
        assertEquals("Parent1", api.types().get(2).parentTypes().get(0).name());
        assertEquals("Parent2", api.types().get(2).parentTypes().get(1).name());
        assertEquals("subName", ((ObjectTypeDeclaration)api.types().get(2)).properties().get(2).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(2)).properties().get(2).type());
    }

    // Should you try the same test with a text file, you get the same result.  'sweird.
    @Test
    public void unionType() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("string | integer")
                        )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("string | integer", api.types().get(0).type());
        assertEquals("string | integer", ((UnionTypeDeclaration)api.types().get(0)).of().get(0).name());
        assertEquals("string | integer", ((UnionTypeDeclaration)api.types().get(0)).of().get(1).name());
    }

    // unions of declared types, they work ok.
    @Test
    public void complexUnions() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Parent1")
                                .ofType(TypeShapeBuilder.type("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.type("string")))
                                ),
                        AnyShapeBuilder.typeDeclaration("Parent2")
                                .ofType(TypeShapeBuilder.type("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name2", TypeShapeBuilder.type("string")))
                                ),


                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.type("Parent1 | Parent2")
                                        .withProperty(
                                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.type("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(2).name());
        assertEquals("Parent1", ((UnionTypeDeclaration)api.types().get(2)).of().get(0).name());
        assertEquals("Parent2", ((UnionTypeDeclaration)api.types().get(2)).of().get(1).name());
    }


}