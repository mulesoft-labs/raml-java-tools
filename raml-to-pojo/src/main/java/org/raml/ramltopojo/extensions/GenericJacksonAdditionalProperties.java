package org.raml.ramltopojo.extensions;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.EventType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class GenericJacksonAdditionalProperties extends ObjectTypeHandlerPlugin.Helper {

    private static final ParameterizedTypeName ADDITIONAL_PROPERTIES_TYPE = ParameterizedTypeName.get(
            Map.class, String.class,
            Object.class);

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration obj, TypeSpec.Builder typeSpec, EventType eventType) {

        if (!obj.additionalProperties()) {

            return typeSpec;
        }

        if (eventType != EventType.IMPLEMENTATION) {


            typeSpec.addMethod(MethodSpec.methodBuilder("getAdditionalProperties")
                    .returns(ADDITIONAL_PROPERTIES_TYPE).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(JsonAnyGetter.class)
                    .build());

            typeSpec.addMethod(MethodSpec
                    .methodBuilder("setAdditionalProperties")
                    .returns(TypeName.VOID)
                    .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(String.class), "key").build())
                    .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(Object.class), "value").build())
                    .addAnnotation(JsonAnySetter.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());

        } else {

            TypeName newSpec = objectPluginContext.createSupportClass(TypeSpec.classBuilder("FooFoo").addModifiers(Modifier.PUBLIC));
            typeSpec.addField(FieldSpec.builder(newSpec, "foo", Modifier.PRIVATE).build());

            typeSpec.addField(FieldSpec
                    .builder(ADDITIONAL_PROPERTIES_TYPE, "additionalProperties", Modifier.PRIVATE)
                    .addAnnotation(AnnotationSpec.builder(JsonIgnore.class).build())
                    .initializer(
                            CodeBlock.of("new $T()",
                                    ParameterizedTypeName.get(HashMap.class, String.class, Object.class))).build());


            typeSpec.addMethod(MethodSpec.methodBuilder("getAdditionalProperties")
                    .returns(ADDITIONAL_PROPERTIES_TYPE).addModifiers(Modifier.PUBLIC)
                    .addCode("return additionalProperties;\n").addAnnotation(JsonAnyGetter.class).build());

            typeSpec.addMethod(MethodSpec
                    .methodBuilder("setAdditionalProperties")
                    .returns(TypeName.VOID)
                    .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(String.class), "key").build())
                    .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(Object.class), "value").build())
                    .addAnnotation(JsonAnySetter.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.builder().add("this.additionalProperties.put(key, value);\n").build())
                    .build());

        }

        return typeSpec;
    }
}
