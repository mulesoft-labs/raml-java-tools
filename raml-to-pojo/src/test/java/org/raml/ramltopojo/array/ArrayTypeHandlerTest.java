package org.raml.ramltopojo.array;

import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ArrayPluginContext;
import org.raml.ramltopojo.extensions.ArrayTypeHandlerPlugin;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;
import org.raml.testutils.UnitTest;
import webapi.WebApiDocument;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.raml.ramltopojo.RamlLoader.findShape;

/**
 * Created. There, you have it.
 */
public class ArrayTypeHandlerTest extends UnitTest {

    @Mock
    private ArrayShape arrayTypeDeclaration;

    @Mock
    private Shape parentType;

    @Mock
    private GenerationContext context;

    @Mock
    private ArrayTypeHandlerPlugin arrayTypeHandlerPlugin;

    @Mock
    private ReferenceTypeHandlerPlugin referencePlugin;

    @Mock
    private ScalarShape itemType;


    @Before
    public void before() {
        when(context.pluginsForArrays(any(Shape.class))).thenReturn(arrayTypeHandlerPlugin);
        when(context.pluginsForReferences(any(Shape.class))).thenReturn(referencePlugin);
        when(arrayTypeDeclaration.items()).thenReturn(itemType);
    }

    @Test
    public void javaClassReferenceWithString() {

        when(arrayTypeDeclaration.items()).thenReturn(new ScalarShape().withDataType(ScalarTypes.STRING_SCALAR));
        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ParameterizedTypeName.get(List.class, String.class)))).thenReturn(ParameterizedTypeName.get(List.class, String.class));
        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ClassName.get(String.class)))).thenReturn(ClassName.get(String.class));

        ArrayTypeHandler handler = new ArrayTypeHandler("string[]", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("java.util.List<java.lang.String>", tn.toString());

    }

    @Test
    public void javaClassReferenceWithListOfSomething() {

        when(context.buildDefaultClassName(any(), any())).thenReturn(ClassName.OBJECT);
        when(arrayTypeDeclaration.items()).thenReturn(new NodeShape().withName("Something"));
        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ParameterizedTypeName.get(List.class, String.class)))).thenReturn(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("foo.Something")));
        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), any())).thenReturn(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("foo.Something")));

        ArrayTypeHandler handler = new ArrayTypeHandler("Something[]", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("java.util.List<foo.Something>", tn.toString());

    }

    @Test
    public void arrayAsType() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("arrays-generation.raml"));
        ArrayTypeHandler handler = new ArrayTypeHandler("foo", findShape("typearray", api.declares()));


        GenerationContextImpl generationContext = new GenerationContextImpl(api);
        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertFalse(r.getImplementation().isPresent());
        assertEquals(r.getInterface().name, "Foo");
        assertEquals(r.getInterface().superclass, ParameterizedTypeName.get(ClassName.get(ArrayList.class),  ClassName.get(String.class)));
    }

    @Test
    public void bracketArrayAsType() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("arrays-generation.raml"));
        ArrayTypeHandler handler = new ArrayTypeHandler("foo", findShape("typebracketarray", api.declares()));


        GenerationContextImpl generationContext = new GenerationContextImpl(api);
        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertFalse(r.getImplementation().isPresent());
        assertEquals(r.getInterface().name, "Foo");
        assertEquals(r.getInterface().superclass, ParameterizedTypeName.get(ClassName.get(ArrayList.class),  ClassName.get(String.class)));
    }

    @Test
    public void javaClassReferenceWithSomethingAsList() {

        ArrayShape shape = new ArrayShape().withItems(new NodeShape().withName("Something"));
        when(arrayTypeHandlerPlugin.className(any(ArrayPluginContext.class), any(ArrayShape.class), eq(null), eq(EventType.INTERFACE))).thenReturn(ClassName.get("foo", "Something"));

        ArrayTypeHandler handler = new ArrayTypeHandler("Something", shape);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("foo.Something", tn.toString());

    }

}