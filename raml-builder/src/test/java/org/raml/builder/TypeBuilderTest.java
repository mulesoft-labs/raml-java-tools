package org.raml.builder;

import amf.client.model.domain.ScalarShape;
import org.junit.Test;
import webapi.WebApiDocument;

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
/*
        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.simpleType("boolean")))
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("boolean", api.types().get(0).type());*/
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

/*        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.simpleType("integer").enumValues(1,2,3)))
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("integer", api.types().get(0).type());
        assertEquals(Arrays.asList(1L,2L,3L), ((IntegerTypeDeclaration)api.types().get(0)).enumValues());*/

    }

    @Test
    public void enumeratedString() {

/*        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        AnyShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.simpleType("string").enumValues("1", "2", "3")))
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("string", api.types().get(0).type());
        assertEquals(Arrays.asList("1","2","3"), ((StringTypeDeclaration)api.types().get(0)).enumValues());*/
    }

    @Test
    public void complexType() {

/*
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
                                )
                )
                .buildModel();

        assertEquals("Mom", api.types().get(0).name());
        assertEquals("object", api.types().get(0).type());
        assertEquals("name", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).name());
        assertEquals("string", ((ObjectTypeDeclaration)api.types().get(0)).properties().get(0).type());
*/
    }

    @Test
    public void complexInheritance() {

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