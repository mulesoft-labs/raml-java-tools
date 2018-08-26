package org.raml.ramltopojo.array;

import com.squareup.javapoet.ClassName;
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
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class ArrayTypeHandlerTest extends UnitTest {

    @Mock
    private ArrayTypeDeclaration arrayTypeDeclaration;

    @Mock
    private TypeDeclaration parentType;

    @Mock
    private GenerationContext context;

    @Mock
    private ArrayTypeHandlerPlugin arrayTypeHandlerPlugin;

    @Mock
    private ReferenceTypeHandlerPlugin referencePlugin;

    @Mock
    private StringTypeDeclaration itemType;


    @Before
    public void before() {
        when(context.pluginsForArrays((TypeDeclaration) any())).thenReturn(arrayTypeHandlerPlugin);
        when(context.pluginsForReferences((TypeDeclaration) any())).thenReturn(referencePlugin);
        when(arrayTypeDeclaration.items()).thenReturn(itemType);
    }

    @Test
    public void javaClassReferenceWithString() {


        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), (TypeName) any())).thenReturn(ClassName.get(String.class));
        when(itemType.name()).thenReturn("string");
        when(itemType.type()).thenReturn("string");

        ArrayTypeHandler handler = new ArrayTypeHandler("string[]", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("java.util.List<java.lang.String>", tn.toString());

    }

    @Test
    public void javaClassReferenceWithListOfSomething() {


        when(referencePlugin.typeName(any(ReferencePluginContext.class), any(TypeDeclaration.class), (TypeName) any())).thenReturn(ClassName.get("foo", "Something"));
        when(itemType.name()).thenReturn("Something");
        when(itemType.type()).thenReturn("object");

        ArrayTypeHandler handler = new ArrayTypeHandler("Something[]", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("java.util.List<foo.Something>", tn.toString());

    }

    @Test
    public void javaClassReferenceWithSomethingAsList() {


        when(arrayTypeHandlerPlugin.className(any(ArrayPluginContext.class), any(ArrayTypeDeclaration.class), (ClassName) eq(null), eq(EventType.INTERFACE))).thenReturn(ClassName.get("foo", "Something"));
        when(itemType.name()).thenReturn("Something");
        when(itemType.type()).thenReturn("object");

        ArrayTypeHandler handler = new ArrayTypeHandler("Something", arrayTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
        assertEquals("foo.Something", tn.toString());

    }

}