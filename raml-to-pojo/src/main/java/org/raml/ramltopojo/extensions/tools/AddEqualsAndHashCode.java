package org.raml.ramltopojo.extensions.tools;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.AllTypesPluginHelper;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;

/**
 * Created. There, you have it.
 */
public class AddEqualsAndHashCode extends AllTypesPluginHelper {

    private final List<String> arguments;

    public AddEqualsAndHashCode(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, final TypeSpec.Builder incoming, EventType eventType) {


        if (eventType == EventType.IMPLEMENTATION) {

            List<FieldSpec> specs = FluentIterable.from(incoming.build().fieldSpecs).filter(new Predicate<FieldSpec>() {
                @Override
                public boolean apply(@Nullable FieldSpec input) {
                    if ( arguments.isEmpty()) {
                        return true;
                    } else {

                        return arguments.contains(input.name);
                    }
                }
            }).toList();

            if ( specs.isEmpty()) {
                return incoming;
            }

            createEquals(specs, incoming);
            createHashCode(specs, incoming);
            return incoming;
        } else {

            return super.classCreated(objectPluginContext, ramlType, incoming, eventType);
        }
    }

    private void createHashCode(List<FieldSpec> specs, TypeSpec.Builder incoming) {

        String fields = FluentIterable.from(specs).transform(new Function<FieldSpec, String>() {
            @Nullable
            @Override
            public String apply(@Nullable FieldSpec fieldSpec) {
                return fieldSpec.name;
            }
        }).join(Joiner.on(","));
        MethodSpec.Builder method = MethodSpec.methodBuilder("hashCode")
                .addAnnotation(ClassName.get(Override.class)).addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT)
                .addCode(CodeBlock.builder().addStatement("return $T.hash($L)", Objects.class, fields).build());
        incoming.addMethod(
                method.build()
        );
    }

    private String buildEquals(List<FieldSpec> fields) {

        return FluentIterable.from(fields).transform(new Function<FieldSpec, String>() {
            @Nullable
            @Override
            public String apply(@Nullable FieldSpec fieldSpec) {
                if ( fieldSpec.type.isPrimitive()) {
                    return CodeBlock.builder().add("(this.$L == other.$L)", fieldSpec.name, fieldSpec.name).build().toString();
                } else {
                    return CodeBlock.builder().add("$T.equals(this.$L, other.$L)", Objects.class, fieldSpec.name, fieldSpec.name).build().toString();
                }
            }
        }).join(Joiner.on(" && "));
    }

    private void createEquals(List<FieldSpec> specs, TypeSpec.Builder incoming) {

        String cb = buildEquals(specs);

        MethodSpec.Builder method = MethodSpec.methodBuilder("equals")
                .addAnnotation(ClassName.get(Override.class)).addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(ParameterSpec.builder(ClassName.get(Object.class), "o").build())
                .addCode(CodeBlock.builder()
                        .addStatement("if (o == null) return false")
                        .addStatement("if (this == o) return true")
                        .addStatement("if (getClass() != o.getClass()) return false").addStatement("$L other = ($L) o", incoming.build().name, incoming.build().name)
                        .addStatement("return " + cb).build()
                );
        incoming.addMethod(
                method.build()
        );
    }

    @Override
    public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {

        if ( eventType != EventType.IMPLEMENTATION) {

            return incoming;
        }

        List<FieldSpec> specs = incoming.build().fieldSpecs;
        createEquals(specs, incoming);
        createHashCode(specs, incoming);
        return incoming;
    }
}
