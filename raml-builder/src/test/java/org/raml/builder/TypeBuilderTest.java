package org.raml.builder;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.ScalarShape;
import org.junit.Test;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.raml.builder.RamlDocumentBuilder.document;

/**
 * Created. There, you have it.
 */
public class TypeBuilderTest {


    @Test
    public void justATypeMam() {

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom").ofType(ScalarShapeBuilder.stringScalar())
                )
                .buildModel();

        assertEquals("Mom", ((ScalarShape)api.declares().get(0)).name().value());
        assertTrue(((ScalarShape)api.declares().get(0)).dataType().value().contains("string"));
    }

    @Test
    public void simpleType() {
        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom")
                                .ofType(ScalarShapeBuilder.booleanScalar())
                )
                .buildModel();

        assertEquals("Mom", ((ScalarShape)api.declares().get(0)).name().value());
        assertTrue(((ScalarShape)api.declares().get(0)).dataType().value().contains("boolean"));
    }

    // the absolutely stupidest type ever.
    @Test
    public void enumeratedBoolean() {

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom").ofType(EnumShapeBuilder.enumeratedType().enumValues(true, false))
                )
                .buildModel();

        assertEquals("Mom", ((ScalarShape)api.declares().get(0)).name().value());
        assertTrue(((ScalarShape)api.declares().get(0)).dataType().value().contains("boolean"));
        assertEquals(2, ((ScalarShape) api.declares().get(0)).values().size());
    }

    @Test
    public void enumeratedInteger() {

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom").ofType(EnumShapeBuilder.enumeratedType().enumValues(1, 2, 3))
                )
                .buildModel();

        assertEquals("Mom", ((ScalarShape)api.declares().get(0)).name().value());
        assertTrue(((ScalarShape)api.declares().get(0)).dataType().value().contains("integer"));
        assertEquals(3, ((ScalarShape) api.declares().get(0)).values().size());

    }

    @Test
    public void enumeratedString() {

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom").ofType(EnumShapeBuilder.enumeratedType().enumValues("1", "2", "3"))
                )
                .buildModel();

        assertEquals("Mom", ((ScalarShape)api.declares().get(0)).name().value());
        assertTrue(((ScalarShape)api.declares().get(0)).dataType().value().contains("string"));
        assertEquals(3, ((ScalarShape) api.declares().get(0)).values().size());

    }

    @Test
    public void complexType() {

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom")
                                .ofType(NodeShapeBuilder.inheritingObject()
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.simpleType("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", ((NodeShape)api.declares().get(0)).name().value());
        assertEquals(0, (((NodeShape) api.declares().get(0)).inherits().size()));
        assertEquals("name", ((NodeShape)api.declares().get(0)).properties().get(0).name().value());
        assertTrue(((NodeShape)api.declares().get(0)).properties().get(0).range().name().value().contains("string"));
    }

    @Test
    public void complexInheritance() throws ExecutionException, InterruptedException {

        DeclaredShapeBuilder parent = DeclaredShapeBuilder.typeDeclaration("Parent")
                .ofType(NodeShapeBuilder.inheritingObject()
                        .withProperty(
                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.simpleType("string")))
                );

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom")
                                .ofType(NodeShapeBuilder.inheritingObject("Parent")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.simpleType("string")))
                                ),
                        parent

                )
                .buildModel();

        System.err.println(Raml10.generateString(api).get());

        assertEquals("Mom", ((NodeShape)api.declares().get(0)).name().value());
        assertEquals(0, (((NodeShape) api.declares().get(0)).inherits().size()));
        assertEquals("name", ((NodeShape)api.declares().get(0)).properties().get(0).name().value());
        assertTrue(((NodeShape)api.declares().get(0)).properties().get(0).range().name().value().contains("string"));
 /*       Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Parent")
                                .ofType(TypeShapeBuilder.simpleType("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.simpleType("string")))
                                ),

                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.simpleType("Parent")
                                        .withProperty(
                                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.simpleType("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(1).name());
        assertEquals("Parent", api.types().get(1).type());
        assertEquals("name", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).type());
        assertEquals("subName", ((ObjectTypeDeclaration)api.types().get(1)).properties().get(1).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(1)).properties().get(1).type());*/
    }

    @Test
    public void multipleInheritance() {

/*        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Parent1")
                                .ofType(TypeShapeBuilder.simpleType("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.simpleType("string")))
                                ),
                        AnyShapeBuilder.typeDeclaration("Parent2")
                                .ofType(TypeShapeBuilder.simpleType("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name2", TypeShapeBuilder.simpleType("string")))
                                ),


                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.inheritingObject("Parent1", "Parent2")
                                        .withProperty(
                                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.simpleType("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(2).name());
        assertEquals("Parent1", api.types().get(2).parentTypes().get(0).name());
        assertEquals("Parent2", api.types().get(2).parentTypes().get(1).name());
        assertEquals("subName", ((ObjectTypeDeclaration)api.types().get(2)).properties().get(2).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(2)).properties().get(2).type());*/
    }

    // Should you try the same test with a text file, you get the same result.  'sweird.
    @Test
    public void unionType() {

/*
        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.simpleType("string | integer")
                        )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("string | integer", api.types().get(0).type());
        assertEquals("string | integer", ((UnionTypeDeclaration)api.types().get(0)).of().get(0).name());
        assertEquals("string | integer", ((UnionTypeDeclaration)api.types().get(0)).of().get(1).name());
*/
    }

    // unions of declared types, they work ok.
    @Test
    public void complexUnions() {

 /*       Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Parent1")
                                .ofType(TypeShapeBuilder.simpleType("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.simpleType("string")))
                                ),
                        AnyShapeBuilder.typeDeclaration("Parent2")
                                .ofType(TypeShapeBuilder.simpleType("object")
                                        .withProperty(
                                                PropertyShapeBuilder.property("name2", TypeShapeBuilder.simpleType("string")))
                                ),


                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.simpleType("Parent1 | Parent2")
                                        .withProperty(
                                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.simpleType("string")))
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(2).name());
        assertEquals("Parent1", ((UnionTypeDeclaration)api.types().get(2)).of().get(0).name());
        assertEquals("Parent2", ((UnionTypeDeclaration)api.types().get(2)).of().get(1).name());*/
    }


}