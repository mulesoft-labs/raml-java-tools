package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.*;
import org.junit.Before;
import org.junit.Test;
import webapi.WebApiParser;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.raml.ramltopojo.RamlLoader.findShape;

/**
 * Created. There, you have it.
 */
public class UtilsTest {

    @Before
    public void before() throws ExecutionException, InterruptedException {

        WebApiParser.init().get();
    }

    @Test
    public void inheritedSquareBrackets() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("type-layout-tests.raml"));
        NodeShape shape = findShape("inheritsSquareBrackets", api.declares());

        List<PropertyShape> properties = Utils.allProperties(shape);
        assertEquals(1, properties.size());
        assertEquals("oneName", properties.get(0).name().value());

        List<AnyShape> inherited = Utils.allParents(shape);
        assertEquals(2, inherited.size());
        assertEquals("inheritsSquareBrackets", inherited.get(0).name().value());
        assertEquals("parentOne", inherited.get(1).name().value());

    }

    @Test
    public void inheritedSquareBracketsFixGuard() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("type-layout-tests.raml"));
        NodeShape shape = findShape("inheritsSquareBrackets", api.declares());

        List<PropertyShape> properties = shape.properties();
        assertEquals(0, properties.size());

        // Weird behaviour in AMF:  if inherited is not a list, no properties.
        List<Shape> inherited = shape.inherits();
        assertEquals(1, inherited.size());
        assertEquals(1, ((NodeShape)inherited.get(0)).properties().size());
    }

    @Test
    public void inheritedNoSquareBrackets() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("type-layout-tests.raml"));
        NodeShape shape = findShape("inheritsNoBrackets", api.declares());

        List<PropertyShape> properties = Utils.allProperties(shape);
        assertEquals(1, properties.size());
        assertEquals("oneName", properties.get(0).name().value());

        List<AnyShape> inherited = Utils.allParents(shape);
        assertEquals(2, inherited.size());
        assertEquals("inheritsNoBrackets", inherited.get(0).name().value());
        assertEquals("parentOne", inherited.get(1).name().value());
    }

    @Test
    public void inheritedNoSquareBracketsFixGuard() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("type-layout-tests.raml"));
        NodeShape shape = findShape("inheritsNoBrackets", api.declares());

        List<PropertyShape> properties = shape.properties();
        assertEquals(0, properties.size());

        // Weird behaviour in AMF:  if inherited is not a list, no properties.....
        List<Shape> inherited = shape.inherits();
        assertEquals(1, inherited.size());
        assertEquals(0, ((NodeShape)inherited.get(0)).properties().size());
    }

    @Test
    public void unionOfScalars() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("type-layout-tests.raml"));
        UnionShape shape = findShape("unionOfScalars", api.declares());

        assertEquals(2, ((UnionShape)shape.inherits().get(0)).anyOf().size());
    }

    @Test
    public void unionOfTypes() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("type-layout-tests.raml"));
        NodeShape shape = findShape("unionOfNodes", api.declares());

        assertEquals(2, ((UnionShape)shape.inherits().get(0)).anyOf().size());
    }

    @Test
    public void unionOfMixed() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("type-layout-tests.raml"));
        UnionShape shape = findShape("unionOfMixed", api.declares());

        assertEquals(2, ((UnionShape)shape.inherits().get(0)).anyOf().size());
    }

}