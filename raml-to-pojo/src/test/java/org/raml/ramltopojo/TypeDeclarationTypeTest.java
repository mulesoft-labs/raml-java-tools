package org.raml.ramltopojo;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;
import org.raml.testutils.UnitTest;
import webapi.WebApiDocument;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class TypeDeclarationTypeTest extends UnitTest {

    @Mock
    ScalarShape integerTypeDeclaration;

    @Mock
    ScalarShape numberTypeDeclaration;

    @Mock
    GenerationContext context;

    @Mock
    private ReferenceTypeHandlerPlugin plugin;


    @Test
    public void internalIntIsNotNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "internalInt");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }


    @Test
    public void simpleObjectIsNotNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "unextended");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void extendedObjectIsNotNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "extendedFromOne");

        assertFalse(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void extendedObjectWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "extendedFromOneWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void objectWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "objectWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void multiInheritanceWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "multiInheritanceWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void multiInheritanceWithoutExtraPropertiesIsNotNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "multiInheritanceWithoutExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Test
    public void arraySimpleType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-array-types.raml"));
        NodeShape decl = RamlLoader.findShape("father", api.declares());

        assertFalse(TypeDeclarationType.isNewInlineType(findProperty(decl, "others")));
        assertFalse(TypeDeclarationType.isNewInlineType(findProperty(decl, "some")));
    }

    @Test
    public void inlineArrayOfDifferentArrayType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-array-types.raml"));
        NodeShape decl = RamlLoader.findShape("mother", api.declares());

        assertTrue(TypeDeclarationType.isNewInlineType(findProperty(decl, "complicatedChildren")));
    }


    @Test
    public void integerType() {

        createIntegerTypeNameSetup(integerTypeDeclaration, TypeDeclarationType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.INT));
    }

    @Test
    public void integerTypeWithByteFormat() {

        // todo when(integerTypeDeclaration.format()).thenReturn(new StrField("int8"));
        createIntegerTypeNameSetup(integerTypeDeclaration, TypeDeclarationType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.BYTE));
    }

    @Test
    public void integerTypeWithDoubleFormat() {

        // todo when(integerTypeDeclaration.format()).thenReturn("double");
        createIntegerTypeNameSetup(integerTypeDeclaration, TypeDeclarationType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.DOUBLE));
    }

    @Test
    public void numberType() {

        createIntegerTypeNameSetup(numberTypeDeclaration, TypeDeclarationType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ClassName.get(Number.class)));
    }

    @Test
    public void numberTypeWithByteFormat() {

        // todo when(numberTypeDeclaration.format()).thenReturn("int8");
        createIntegerTypeNameSetup(numberTypeDeclaration, TypeDeclarationType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.BYTE));
    }

    @Test
    public void numberTypeWithDoubleFormat() {

        // todo when(numberTypeDeclaration.format()).thenReturn("double");
        createIntegerTypeNameSetup(numberTypeDeclaration, TypeDeclarationType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.DOUBLE));
    }

    private void createIntegerTypeNameSetup(ScalarShape integerTypeDeclaration, TypeDeclarationType typeDeclarationType) {
        when(context.pluginsForReferences(integerTypeDeclaration)).thenReturn(plugin);
        TypeHandler handler = typeDeclarationType.createHandler("foo", TypeDeclarationType.INTEGER, null /*integerTypeDeclaration*/);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
    }

    @Test @Ignore
    public void unionWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "unionWithExtraProperty");

        assertTrue(TypeDeclarationType.isNewInlineType(property));
    }

    @Deprecated() /* return optional.....*/
    protected PropertyShape findProperty(NodeShape decl, final String propertyName) {
        return decl.properties().stream().filter(input -> propertyName.equals(input.name().value())).findFirst().orElse(null);
    }

}