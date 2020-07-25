package org.raml.builder;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.UnionShape;
import amf.client.validate.ValidationReport;
import org.junit.Test;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.raml.builder.RamlDocumentBuilder.document;

/**
 * Created. There, you have it.
 */
public class TypeBuilderTest {

    @Test
    public void goddamit() throws ExecutionException, InterruptedException {


        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun").buildNode();

        NodeShape parent = createParent();

        NodeShape child = new NodeShape();
        child.withName("child");
        child.withId("amf://id#4");

        PropertyShape propertyChild = new PropertyShape();
        propertyChild.withPath("prop2");
        propertyChild.withId("amf://id#5");
        propertyChild.withName("prop2");
        ScalarShape rangeChild = new ScalarShape().withDataType("http://www.w3.org/2001/XMLSchema#string");
        propertyChild.withRange(rangeChild);
        rangeChild.withId("amf://id#6");
        child.withProperties(Collections.singletonList(propertyChild));
        child.withInherits(Collections.singletonList(parent));

        api.withDeclares(Arrays.asList(parent, child));


        ValidationReport s = Raml10.validate(api).get();
        if (!s.conforms()) {
            throw new ModelBuilderException(s);
        }

        System.err.println(Raml10.generateString(api).get());
    }

    public NodeShape createParent() {
        NodeShape parent = new NodeShape();
        parent.withId("amf://id#1");

        PropertyShape property = new PropertyShape();
        property.withPath("prop1");
        property.withId("amf://id#2");
        property.withName("prop1");
        ScalarShape range = new ScalarShape().withDataType("http://www.w3.org/2001/XMLSchema#string");
        property.withRange(range);
        range.withId("amf://id#3");
        parent.withName("parent");
        parent.withProperties(Collections.singletonList(property));
        return parent;
    }

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
                                .ofType(NodeShapeBuilder.inheritingObjectFromShapes()
                                        .withProperty(
                                                PropertyShapeBuilder.property("name", TypeShapeBuilder.stringScalar()))
                                )
                )
                .buildModel();

        assertEquals("Mom", ((NodeShape)api.declares().get(0)).name().value());
        assertEquals(0, (((NodeShape) api.declares().get(0)).inherits().size()));
        assertEquals("name", ((NodeShape)api.declares().get(0)).properties().get(0).name().value());
        assertTrue(((NodeShape)api.declares().get(0)).properties().get(0).range().name().value().contains("string"));
    }

    //@Test
    // With ids, inheritance dissapears......whc is funny, because the output is still slightly incorrect.
    public void complexInheritance() throws ExecutionException, InterruptedException {

        DeclaredShapeBuilder<?> parent = DeclaredShapeBuilder.typeDeclaration("Parent")
                .ofType(NodeShapeBuilder.inheritingObjectFromShapes()
                        .withProperty(
                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.stringScalar()))
                );

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom")
                                .ofType(NodeShapeBuilder.inheritingObjectFromShapes(parent.buildNode()).withProperty(PropertyShapeBuilder.property("name", TypeShapeBuilder.stringScalar()))
                                )
                )
                .buildModel();


        assertEquals("Mom", ((NodeShape)api.declares().get(0)).name().value());
        assertEquals(1, (((NodeShape) api.declares().get(0)).inherits().size()));
        assertEquals(1, ((NodeShape)api.declares().get(0)).properties().size());
        assertEquals("name", ((NodeShape)api.declares().get(0)).properties().get(0).name().value());
        assertTrue(((NodeShape)api.declares().get(0)).properties().get(0).range().name().value().contains("string"));
    }


    @Test
    public void multipleInheritance() {

        DeclaredShapeBuilder<?> parent1 = DeclaredShapeBuilder.typeDeclaration("Parent1")
                .ofType(NodeShapeBuilder.inheritingObjectFromShapes()
                        .withProperty(
                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.stringScalar()))
                );

        DeclaredShapeBuilder<?> parent2 = DeclaredShapeBuilder.typeDeclaration("Parent2")
                .ofType(NodeShapeBuilder.inheritingObjectFromShapes()
                        .withProperty(
                                PropertyShapeBuilder.property("subName2", TypeShapeBuilder.stringScalar()))
                );

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom")
                                .ofType(NodeShapeBuilder.inheritingObjectFromShapes(parent1.buildNode(), parent2.buildNode()).withProperty(PropertyShapeBuilder.property("name", TypeShapeBuilder.stringScalar()))
                                )
                )
                .buildModel();


        assertEquals("Mom", ((NodeShape) api.declares().get(0)).name().value());
        assertEquals(0, (((NodeShape) api.declares().get(0)).inherits().size()));
        assertTrue(((NodeShape) api.declares().get(0)).properties().get(0).range().name().value().contains("string"));
        assertEquals("name", ((NodeShape) api.declares().get(0)).properties().get(1).name().value());
        assertEquals("subName", ((NodeShape) ((NodeShape) api.declares().get(0))).properties().get(2).name().value());
        assertEquals("subName2", ((NodeShape) ((NodeShape) api.declares().get(0))).properties().get(0).name().value());

    }

    // Should you try the same test with a text file, you get the same result.  'sweird.
    @Test
    public void unionType() {

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.unionShapeOf(
                                        TypeShapeBuilder.stringScalar().buildNode(),
                                        TypeShapeBuilder.longScalar().buildNode()
                                        )
                                )
                )
                .buildModel();


        assertEquals("Mom", ((UnionShape) api.declares().get(0)).name().value());
        assertTrue(((ScalarShape)((UnionShape)api.declares().get(0)).anyOf().get(0)).dataType().value().contains("string"));
        assertTrue(((ScalarShape)((UnionShape)api.declares().get(0)).anyOf().get(1)).dataType().value().contains("long"));
    }

    // unions of declared types, they work ok.
    @Test
    public void complexUnions() {


        DeclaredShapeBuilder<?> parent1 = DeclaredShapeBuilder.typeDeclaration("Parent1")
                .ofType(NodeShapeBuilder.inheritingObjectFromShapes()
                        .withProperty(
                                PropertyShapeBuilder.property("subName", TypeShapeBuilder.stringScalar()))
                );

        DeclaredShapeBuilder<?> parent2 = DeclaredShapeBuilder.typeDeclaration("Parent2")
                .ofType(NodeShapeBuilder.inheritingObjectFromShapes()
                        .withProperty(
                                PropertyShapeBuilder.property("subName2", TypeShapeBuilder.stringScalar()))
                );

        WebApiDocument api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withTypes(
                        DeclaredShapeBuilder.typeDeclaration("Mom")
                                .ofType(TypeShapeBuilder.unionShapeOf(parent1.buildNode(), parent2.buildNode()).withProperty(PropertyShapeBuilder.property("name", TypeShapeBuilder.stringScalar()))
                                )
                )
                .buildModel();

        assertEquals("Mom", ((UnionShape) api.declares().get(0)).name().value());
        assertEquals("Parent1", ((UnionShape)api.declares().get(0)).anyOf().get(0).name().value());
        assertEquals("Parent2", ((UnionShape)api.declares().get(0)).anyOf().get(1).name().value());
    }


}