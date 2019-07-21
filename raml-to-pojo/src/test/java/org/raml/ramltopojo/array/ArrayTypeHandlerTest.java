package org.raml.ramltopojo.array;

import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationContext;
import org.raml.ramltopojo.extensions.ArrayPluginContext;
import org.raml.ramltopojo.extensions.ArrayTypeHandlerPlugin;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;
import org.raml.testutils.UnitTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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


        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ParameterizedTypeName.get(List.class, String.class)))).thenReturn(ParameterizedTypeName.get(List.class, String.class));
        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ClassName.get(String.class)))).thenReturn(ClassName.get(String.class));

        // todo when(itemType.name()).thenReturn("string");
        // todo when(itemType.type()).thenReturn("string");

        ArrayTypeHandler handler = new ArrayTypeHandler("string[]", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("java.util.List<java.lang.String>", tn.toString());

    }

    @Test
    public void javaClassReferenceWithListOfSomething() {

        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ParameterizedTypeName.get(List.class, String.class)))).thenReturn(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("foo.Something")));
        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(Shape.class), (TypeName) any())).thenReturn(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.bestGuess("foo.Something")));
        // todo when(itemType.name()).thenReturn("Something");
        // todo when(itemType.type()).thenReturn("object");

        ArrayTypeHandler handler = new ArrayTypeHandler("Something[]", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("java.util.List<foo.Something>", tn.toString());

    }

    @Test
    public void javaClassReferenceWithSomethingAsList() {


        when(arrayTypeHandlerPlugin.className(any(ArrayPluginContext.class), any(ArrayShape.class), eq(null), eq(EventType.INTERFACE))).thenReturn(ClassName.get("foo", "Something"));
        // todo when(itemType.name()).thenReturn("Something");
        // todo when(itemType.type()).thenReturn("object");

        ArrayTypeHandler handler = new ArrayTypeHandler("Something", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("foo.Something", tn.toString());

    }

}