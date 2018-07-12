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

        TypeName newSpec = objectPluginContext.createSupportClass(
                TypeSpec.classBuilder("ExcludingMap")
                        .superclass(ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(Object.class)))
                        .addMethod(
                                MethodSpec.methodBuilder("put")
                                        .addParameter(ClassName.get(String.class), "key")
                                        .addParameter(ClassName.get(Object.class), "value")
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(TypeName.OBJECT)
                                        .addCode(CodeBlock.builder().addStatement("return super.put(key, value)").build())
                                        .build())
                        .addMethod(
                                MethodSpec.methodBuilder("putAll")
                                        .addParameter(ParameterizedTypeName.get(ClassName.get(Map.class), WildcardTypeName.subtypeOf(String.class), WildcardTypeName.subtypeOf(Object.class)), "otherMap")
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(TypeName.VOID)
                                        .addCode(CodeBlock.builder().addStatement("super.putAll(otherMap)").build())
                                        .build())
                        .addModifiers(Modifier.PUBLIC));


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

            typeSpec.addField(FieldSpec
                    .builder(ADDITIONAL_PROPERTIES_TYPE, "additionalProperties", Modifier.PRIVATE)
                    .addAnnotation(AnnotationSpec.builder(JsonIgnore.class).build())
                    .initializer(
                            CodeBlock.of("new $T()",
                                    newSpec)).build());

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
