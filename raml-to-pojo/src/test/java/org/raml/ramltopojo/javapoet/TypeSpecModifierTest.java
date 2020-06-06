package org.raml.ramltopojo.javapoet;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.assertj.core.api.Condition;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created. There, you have it.
 */
public class TypeSpecModifierTest {


    @Test
    public void testMethodModification() {

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder("foo").addMethod(MethodSpec.methodBuilder("someMethod").build());
        TypeSpecModifier.modify(builder).modifyMethods((b) -> MethodSpec.methodBuilder("doodoo").build());

        assertThat(builder.methodSpecs).hasSize(1)
                .areExactly(1, thatAreNamed("doodoo"));
    }

    @Test
    public void testRemoval() {

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder("foo").addMethod(MethodSpec.methodBuilder("someMethod").build());
        TypeSpecModifier.modify(builder).modifyMethods((b) -> null);

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
}