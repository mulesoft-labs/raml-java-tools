package org.raml.ramltopojo.extensions.jsr303;

import amf.client.model.domain.*;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.ScalarTypes;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.testutils.UnitTest;

import javax.lang.model.element.Modifier;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class Jsr303ExtensionTest extends UnitTest {

    @Mock
    ObjectPluginContext objectPluginContext;

    @Mock
    UnionPluginContext unionPluginContext;


    private PropertyShape propertyShapeOfRange(Shape range) {

        return (PropertyShape) new PropertyShape().withRange(range).withName("champ");
    }


    @Test
    public void forInteger() throws Exception {

        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(Integer.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(setupNumberFacets(new ScalarShape().withDataType(ScalarTypes.INTEGER_SCALAR))).withMinCount(1), builder, EventType.IMPLEMENTATION);

        assertForIntegerNumber(builder);
    }


    @Test
    public void forBigInt() throws Exception {

        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(BigInteger.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(setupNumberFacets(new ScalarShape().withDataType(ScalarTypes.INTEGER_SCALAR))).withMinCount(1), builder, EventType.IMPLEMENTATION);

        assertForIntegerNumber(builder);
    }


    @Test
    public void forObject() throws Exception {

        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(Double.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new NodeShape()), builder, EventType.IMPLEMENTATION);

        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forUnion() throws Exception {

        Jsr303Extension ext = new Jsr303Extension();
        TypeSpec.Builder typeBuilder =
                TypeSpec.classBuilder(ClassName.get("xx.bb", "Foo"));

        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(Double.class), "champ", Modifier.PUBLIC);

        ext.anyFieldCreated(unionPluginContext, new UnionShape(), typeBuilder, builder, EventType.IMPLEMENTATION);

        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forDouble() throws Exception {

        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(Double.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new NodeShape()), builder, EventType.IMPLEMENTATION);

        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forArrays() throws Exception {

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new ArrayShape().withMinItems(3).withMaxItems(5)), builder, EventType.IMPLEMENTATION);
        assertEquals(Size.class.getName(), builder.build().annotations.get(0).type.toString());
        assertEquals("3", builder.build().annotations.get(0).members.get("min").get(0).toString());
        assertEquals("5", builder.build().annotations.get(0).members.get("max").get(0).toString());
    }

    @Test
    public void forArraysOfStrings() throws Exception {

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new ArrayShape()), builder, EventType.IMPLEMENTATION);
        assertEquals(0, builder.build().annotations.size());
    }

    @Test
    public void forArraysOfObject() throws Exception {

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new NodeShape()), builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forArraysOfUnion() throws Exception {

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new UnionShape()), builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forArraysMaxOnly() throws Exception {

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new ArrayShape().withMaxItems(5)), builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(Size.class.getName(), builder.build().annotations.get(0).type.toString());
        assertEquals(1, builder.build().annotations.get(0).members.size());
        assertEquals("5", builder.build().annotations.get(0).members.get("max").get(0).toString());
    }

    @Test
    public void forArraysNotNull() throws Exception {

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, propertyShapeOfRange(new ArrayShape()).withMinCount(1), builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(NotNull.class.getName(), builder.build().annotations.get(0).type.toString());
    }


    public ScalarShape setupNumberFacets(ScalarShape scalarShape) {

        return scalarShape.withMinimum(13.0).withMaximum(17.0);
/*
        when(number.minimum()).thenReturn(13.0);
        when(number.maximum()).thenReturn(17.0);
        when(number.required()).thenReturn(true);
*/
    }

    public void assertForIntegerNumber(FieldSpec.Builder builder) {

        assertEquals(3, builder.build().annotations.size());
        assertEquals(NotNull.class.getName(), builder.build().annotations.get(0).type.toString());
        assertEquals(Min.class.getName(), builder.build().annotations.get(1).type.toString());
        assertEquals("13", builder.build().annotations.get(1).members.get("value").get(0).toString());
        assertEquals(Max.class.getName(), builder.build().annotations.get(2).type.toString());
        assertEquals("17", builder.build().annotations.get(2).members.get("value").get(0).toString());
    }

}