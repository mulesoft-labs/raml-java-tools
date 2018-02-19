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
public class AddToString extends AllTypesPluginHelper {

    private final List<String> arguments;

    public AddToString(List<String> arguments) {
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

            createToString(specs, incoming);
            return incoming;
        } else {

            return super.classCreated(objectPluginContext, ramlType, incoming, eventType);
        }
    }

    private void createToString(final List<FieldSpec> fields, TypeSpec.Builder incoming) {

        String s =  FluentIterable.from(fields).transform(new Function<FieldSpec, String>() {
            @Nullable
            @Override
            public String apply(@Nullable FieldSpec fieldSpec) {
                if ( fieldSpec.type.isPrimitive() || fieldSpec.type.equals(ClassName.get(String.class))) {
                    return CodeBlock.builder().add("$S + $T.toString(this.$L)", fieldSpec.name + " = ", Objects.class, fieldSpec.name).build().toString();
                } else {
                    return CodeBlock.builder().add("$S + $T.toString(this.$L) + \"]\"", fieldSpec.name + " = [", Objects.class, fieldSpec.name).build().toString();
                }
            }
        }).join(Joiner.on(" + \", \" + "));

        MethodSpec.Builder method = MethodSpec.methodBuilder("toString")
                .addAnnotation(ClassName.get(Override.class)).addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(String.class))
                .addCode(CodeBlock.builder()
                        .addStatement("return " + s).build()
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
        createToString(specs, incoming);
        return incoming;
    }
}
