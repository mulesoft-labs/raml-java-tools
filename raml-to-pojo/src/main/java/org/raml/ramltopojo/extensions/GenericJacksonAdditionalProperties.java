package org.raml.ramltopojo.extensions;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.EcmaPattern;
import org.raml.ramltopojo.EventType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.regex.Pattern;

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
                buildSpecialMap());


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
                            withProperties(newSpec, obj).build()).build());

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

    private CodeBlock.Builder withProperties(TypeName newSpec, ObjectTypeDeclaration object) {

        List<TypeDeclaration> properties = FluentIterable.from(object.properties()).filter(new Predicate<TypeDeclaration>() {
            @Override
            public boolean apply(@Nullable TypeDeclaration property) {
                return property != null && EcmaPattern.isSlashedPattern(property.name()) && ! EcmaPattern.fromString(property.name()).asJavaPattern().isEmpty();
            }
        }).toList();

        if ( properties.size() == 0) {

            return CodeBlock.builder().addStatement("new $T()", newSpec);
        }

        CodeBlock.Builder cb = CodeBlock.builder().beginControlFlow("new $T(){", newSpec);
        for (TypeDeclaration typeDeclaration : object.properties()) {

                cb.addStatement("addAcceptedPattern($T.compile($S))", Pattern.class, EcmaPattern.fromString(typeDeclaration.name()).asJavaPattern());
        }
        return cb.endControlFlow("}");
    }

    protected TypeSpec.Builder buildSpecialMap() {
        return TypeSpec.classBuilder("ExcludingMap")
                .superclass(ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(Object.class)))
                .addField(
                        FieldSpec.builder(ParameterizedTypeName.get(Set.class, Pattern.class), "additionalProperties")
                                .initializer(CodeBlock.builder().add(" new $T()", ParameterizedTypeName.get(HashSet.class, Pattern.class)).build())
                        .build())
                .addMethod(
                        MethodSpec.methodBuilder("put")
                                .addParameter(ClassName.get(String.class), "key")
                                .addParameter(ClassName.get(Object.class), "value")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(TypeName.OBJECT)
                                .beginControlFlow("if ( additionalProperties.size() == 0 ) ")
                                .addStatement("return super.put(key, value)")
                                .endControlFlow()
                                .beginControlFlow("else")
                                .addStatement("return setProperty(key, value)")
                                .endControlFlow()
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("putAll")
                                .addParameter(ParameterizedTypeName.get(ClassName.get(Map.class), WildcardTypeName.subtypeOf(String.class), WildcardTypeName.subtypeOf(Object.class)), "otherMap")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(TypeName.VOID)
                                .addCode(CodeBlock.builder()
                                        .beginControlFlow("if ( additionalProperties.size() == 0 ) ")
                                        .addStatement("super.putAll(otherMap)")
                                        .endControlFlow()
                                        .beginControlFlow("else")
                                        .beginControlFlow("for ( String key: otherMap.keySet() )")
                                        .addStatement("setProperty(key, otherMap.get(key))")
                                        .endControlFlow()
                                        .endControlFlow()
                                        .build())
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("addAcceptedPattern")
                                .addParameter(ClassName.get(Pattern.class), "pattern")
                                .addModifiers(Modifier.PROTECTED)
                                .returns(TypeName.VOID)
                                .addCode(CodeBlock.builder().addStatement("additionalProperties.add(pattern)").build())
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("setProperty")
                                .addParameter(ClassName.get(String.class), "key")
                                .addParameter(ClassName.get(Object.class), "value")
                                .addModifiers(Modifier.PRIVATE)
                                .returns(TypeName.OBJECT)
                                .beginControlFlow("if ( additionalProperties.size() == 0 ) ")
                                .addStatement("return super.put(key, value)")
                                .endControlFlow()
                                .beginControlFlow("else")
                                .beginControlFlow("for ( $T p : additionalProperties)", Pattern.class)
                                .beginControlFlow("if ( p.matcher(key).matches() )")
                                .addStatement("return super.put(key, value)")
                                .endControlFlow()
                                .endControlFlow()
                                .addStatement("throw new $T(\"property \" + key + \" is invalid according to RAML type\")", IllegalArgumentException.class)
                                .endControlFlow()
                                .build())

                .addModifiers(Modifier.PUBLIC);
    }
}
