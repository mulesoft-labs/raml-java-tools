package org.raml.ramltopojo.javapoet;

import com.squareup.javapoet.*;
import org.assertj.core.api.Condition;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created. There, you have it.
 */
public class TypeSpecModifierTest {

    @Test
    public void testMethodModification() {

        TypeSpec.Builder builder = TypeSpec.classBuilder("foo")
                .addAnnotation(Deprecated.class)
                .addField(FieldSpec.builder(ClassName.get(String.class), "field").build())
                .addSuperinterface(List.class)
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("foo", TypeSpec.classBuilder("justforfun").build())
                .addMethod(MethodSpec.methodBuilder("method").build());

        TypeSpecModifier.modify(builder)
                .modifyMethods((b) -> MethodSpec.methodBuilder("changedMethod").build())
                .modifyAnnotations((b) -> AnnotationSpec.builder(FunctionalInterface.class).build())
                .modifyModifiers((b) -> Modifier.PRIVATE)
                .modifySuperinterfaces(b -> ClassName.get(Map.class))
                .modifyEnumConstant(e -> Map.entry("another", TypeSpec.classBuilder("modified").build()))
                .modifyFields(b -> FieldSpec.builder(ClassName.get(Integer.class), "changedField").build())
                .modifyAll();

        assertThat(builder.methodSpecs).hasSize(1)
                .areExactly(1, thatAreNamed("changedMethod"));

        assertThat(builder.annotations).hasSize(1)
                .areExactly(1, thatAreNamed(ClassName.get(FunctionalInterface.class)));

        assertThat(builder.modifiers).hasSize(1).containsExactly(Modifier.PRIVATE);

        assertThat(builder.superinterfaces).hasSize(1).containsExactly(ClassName.get(Map.class));

        assertThat(builder.enumConstants).hasSize(1).containsKeys("another");
        assertThat(builder.fieldSpecs).hasSize(1)
                .areExactly(1, fieldNamedAndTyped("changedField", ClassName.get(Integer.class)));

    }

    @Test
    public void testRemoval() {

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder("foo").addMethod(MethodSpec.methodBuilder("someMethod").build());
        TypeSpecModifier.modify(builder)
                .modifyMethods((b) -> null)
                .modifyAll();

        assertThat(builder.methodSpecs).hasSize(0);
    }

    private Condition<? super MethodSpec> thatAreNamed(String someMethod) {
        return new Condition<MethodSpec>() {
            @Override
            public boolean matches(MethodSpec value) {
                return value.name.equals(someMethod);
            }
        };
    }

    private Condition<? super FieldSpec> fieldNamedAndTyped(String name, TypeName type) {
        return new Condition<FieldSpec>() {
            @Override
            public boolean matches(FieldSpec value) {
                return value.name.equals(name) && value.type.equals(type);
            }
        };
    }

    private Condition<? super AnnotationSpec> thatAreNamed(TypeName typeName) {
        return new Condition<AnnotationSpec>() {
            @Override
            public boolean matches(AnnotationSpec value) {
                return value.type.equals(typeName);
            }
        };
    }

}