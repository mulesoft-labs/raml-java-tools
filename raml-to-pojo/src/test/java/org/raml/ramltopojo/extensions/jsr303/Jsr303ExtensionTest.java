package org.raml.ramltopojo.extensions.jsr303;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.lang.model.element.Modifier;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class Jsr303ExtensionTest extends UnitTest {

    @Mock
    NumberTypeDeclaration number;

    @Mock
    ArrayTypeDeclaration array;

    @Mock
    ObjectTypeDeclaration object;

    @Mock
    UnionTypeDeclaration union;

    @Mock
    ObjectPluginContext objectPluginContext;

    @Mock
    UnionPluginContext unionPluginContext;


    @Test
    public void forInteger() throws Exception {

        setupNumberFacets();
        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(Integer.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, number, builder, EventType.IMPLEMENTATION);

        assertForIntegerNumber(builder);
    }


    @Test
    public void forBigInt() throws Exception {

        setupNumberFacets();
        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(BigInteger.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, number, builder, EventType.IMPLEMENTATION);

        assertForIntegerNumber(builder);
    }


    @Test
    public void forObject() throws Exception {

        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(Double.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, object, builder, EventType.IMPLEMENTATION);

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

        ext.anyFieldCreated(unionPluginContext, union, typeBuilder, builder, EventType.IMPLEMENTATION);

        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forDouble() throws Exception {

        setupNumberFacets();
        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder =
                FieldSpec.builder(ClassName.get(Double.class), "champ", Modifier.PUBLIC);

        ext.fieldBuilt(objectPluginContext, object, builder, EventType.IMPLEMENTATION);

        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forArrays() throws Exception {

        when(array.minItems()).thenReturn(3);
        when(array.maxItems()).thenReturn(5);

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, array, builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(Size.class.getName(), builder.build().annotations.get(0).type.toString());
        assertEquals("3", builder.build().annotations.get(0).members.get("min").get(0).toString());
        assertEquals("5", builder.build().annotations.get(0).members.get("max").get(0).toString());
    }

    @Test
    public void forArraysOfStrings() throws Exception {

        when(array.minItems()).thenReturn(null);
        when(array.maxItems()).thenReturn(null);

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, array, builder, EventType.IMPLEMENTATION);
        assertEquals(0, builder.build().annotations.size());
    }

    @Test
    public void forArraysOfObject() throws Exception {

        when(array.minItems()).thenReturn(null);
        when(array.maxItems()).thenReturn(null);
        when(array.items()).thenReturn(object);

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, array, builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forArraysOfUnion() throws Exception {

        when(array.minItems()).thenReturn(null);
        when(array.maxItems()).thenReturn(null);
        when(array.items()).thenReturn(union);

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, array, builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(Valid.class.getName(), builder.build().annotations.get(0).type.toString());
    }

    @Test
    public void forArraysMaxOnly() throws Exception {

        when(array.minItems()).thenReturn(null);
        when(array.maxItems()).thenReturn(5);

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, array, builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(Size.class.getName(), builder.build().annotations.get(0).type.toString());
        assertEquals(1, builder.build().annotations.get(0).members.size());
        assertEquals("5", builder.build().annotations.get(0).members.get("max").get(0).toString());
    }

    @Test
    public void forArraysNotNull() throws Exception {

        when(array.minItems()).thenReturn(null);
        when(array.maxItems()).thenReturn(null);
        when(array.required()).thenReturn(true);

        FieldSpec.Builder builder =
                FieldSpec.builder(ParameterizedTypeName.get(List.class, String.class), "champ",
                        Modifier.PUBLIC);
        Jsr303Extension ext = new Jsr303Extension();
        ext.fieldBuilt(objectPluginContext, array, builder, EventType.IMPLEMENTATION);
        assertEquals(1, builder.build().annotations.size());
        assertEquals(NotNull.class.getName(), builder.build().annotations.get(0).type.toString());
    }


    public void setupNumberFacets() {
        when(number.minimum()).thenReturn(13.0);
        when(number.maximum()).thenReturn(17.0);
        when(number.required()).thenReturn(true);
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