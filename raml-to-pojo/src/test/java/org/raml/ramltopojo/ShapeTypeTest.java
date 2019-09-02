package org.raml.ramltopojo;

import amf.client.model.domain.*;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
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
public class ShapeTypeTest extends UnitTest {

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

        assertFalse(ShapeType.isNewInlineType((AnyShape) property.range()));
    }


    @Test
    public void simpleObjectIsNotNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "unextended");

        assertFalse(ShapeType.isNewInlineType((AnyShape) property.range()));
    }

    @Test
    public void extendedObjectIsNotNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "extendedFromOne");

        assertFalse(ShapeType.isNewInlineType((AnyShape) property.range()));
    }

    @Test
    public void extendedObjectWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "extendedFromOneWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType((AnyShape) property.range()));
    }

    @Test
    public void objectWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "objectWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType((AnyShape) property.range()));
    }

    @Test
    public void multiInheritanceWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "multiInheritanceWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType((AnyShape) property.range()));
    }

    @Test
    public void multiInheritanceWithoutExtraPropertiesIsNotNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "multiInheritanceWithoutExtraProperty");

        assertTrue(ShapeType.isNewInlineType((AnyShape) property.range()));
    }

    @Test
    public void arraySimpleType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-array-types.raml"));
        NodeShape decl = RamlLoader.findShape("father", api.declares());

        assertFalse(ShapeType.isNewInlineType((AnyShape) findProperty(decl, "others").range()));
        assertFalse(ShapeType.isNewInlineType((AnyShape) findProperty(decl, "some").range()));
    }

    @Test
    public void inlineArrayOfDifferentArrayType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-array-types.raml"));
        NodeShape decl = RamlLoader.findShape("mother", api.declares());

        assertTrue(ShapeType.isNewInlineType((AnyShape) findProperty(decl, "complicatedChildren").range()));
    }


    @Test
    public void integerType() {

        createIntegerTypeNameSetup(new ScalarShape().withDataType("integer"), ShapeType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.INT));
    }

    @Test
    public void integerTypeWithByteFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("int8").withDataType("integer"), ShapeType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.BYTE));
    }

    @Test
    public void integerTypeWithDoubleFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("double").withDataType("integer"), ShapeType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.DOUBLE));
    }

    @Test
    public void numberType() {

        createIntegerTypeNameSetup(new ScalarShape().withDataType("number"), ShapeType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ClassName.get(Number.class)));
    }

    @Test
    public void numberTypeWithByteFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("int8").withDataType("number"), ShapeType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.BYTE));
    }

    @Test
    public void numberTypeWithDoubleFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("double").withDataType("number"), ShapeType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.DOUBLE));
    }

    private void createIntegerTypeNameSetup(ScalarShape integerTypeDeclaration, ShapeType shapeType) {
        when(context.pluginsForReferences(integerTypeDeclaration)).thenReturn(plugin);
        TypeHandler handler = shapeType.createHandler("foo", ShapeType.INTEGER, integerTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
    }

    @Test
    public void unionWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "unionWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType((AnyShape) property.range()));
    }

    @Deprecated() /* return optional.....*/
    protected PropertyShape findProperty(NodeShape decl, final String propertyName) {
        return decl.properties().stream().filter(input -> propertyName.equals(input.name().value())).findFirst().orElse(null);
    }

}