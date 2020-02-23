package org.raml.ramltopojo;

import amf.client.model.document.Document;
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

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "internalInt");

        assertFalse(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }


    @Test
    public void simpleObjectIsNotNewInlineType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "unextended");

        assertFalse(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }

    @Test
    public void extendedObjectIsNotNewInlineType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "extendedFromOne");

        assertTrue(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }

    @Test
    public void extendedObjectWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "extendedFromOneWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }

    @Test
    public void objectWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "objectWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }

    @Test
    public void multiInheritanceWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "multiInheritanceWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }

    @Test
    public void multiInheritanceWithoutExtraPropertiesIsNotNewInlineType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "multiInheritanceWithoutExtraProperty");

        assertTrue(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }

    @Test
    public void arraySimpleType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-array-types.raml"));
        NodeShape decl = RamlLoader.findShape("father", api.declares());

        assertFalse(ShapeType.isNewInlineType(Utils.rangeOf(findProperty(decl, "others"))));
        assertFalse(ShapeType.isNewInlineType(Utils.rangeOf(findProperty(decl, "some"))));
    }

    @Test @Ignore("inline testing does't work the same in webapi.")
    public void inlineArrayOfDifferentArrayType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-array-types.raml"));
        NodeShape decl = RamlLoader.findShape("mother", api.declares());

        assertTrue(ShapeType.isNewInlineType(Utils.rangeOf(findProperty(decl, "complicatedChildren"))));
    }


    @Test
    public void integerType() {

        createIntegerTypeNameSetup(new ScalarShape().withDataType(ScalarTypes.INTEGER_SCALAR), ShapeType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.INT));
    }

    @Test
    public void integerTypeWithByteFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("int8").withDataType(ScalarTypes.INTEGER_SCALAR), ShapeType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.BYTE));
    }

    @Test
    public void integerTypeWithDoubleFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("double").withDataType(ScalarTypes.INTEGER_SCALAR), ShapeType.INTEGER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.DOUBLE));
    }

    @Test
    public void numberType() {

        createIntegerTypeNameSetup(new ScalarShape().withDataType(ScalarTypes.NUMBER_SCALAR), ShapeType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(ClassName.get(Number.class)));
    }

    @Test
    public void numberTypeWithByteFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("int8").withDataType(ScalarTypes.NUMBER_SCALAR), ShapeType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.BYTE));
    }

    @Test
    public void numberTypeWithDoubleFormat() {

        createIntegerTypeNameSetup(new ScalarShape().withFormat("double").withDataType(ScalarTypes.NUMBER_SCALAR), ShapeType.NUMBER);

        verify(plugin).typeName(any(ReferencePluginContext.class), any(Shape.class), eq(TypeName.DOUBLE));
    }

    private void createIntegerTypeNameSetup(ScalarShape integerTypeDeclaration, ShapeType shapeType) {
        when(context.pluginsForReferences(integerTypeDeclaration)).thenReturn(plugin);
        TypeHandler handler = shapeType.createHandler("foo", ShapeType.INTEGER, integerTypeDeclaration);
        TypeName tn = handler.javaClassReference(context, EventType.INTERFACE);
    }

    @Test
    public void unionWithExtraPropertiesIsNewInlineType() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("inline-types.raml"));
        NodeShape decl = RamlLoader.findShape("foo", api.declares());
        PropertyShape property = findProperty(decl, "unionWithExtraProperty");

        assertTrue(ShapeType.isNewInlineType(Utils.rangeOf(property)));
    }

    @Deprecated() /* return optional.....*/
    protected PropertyShape findProperty(NodeShape decl, final String propertyName) {
        return decl.properties().stream().filter(input -> propertyName.equals(input.name().value())).findFirst().orElse(null);
    }

}