package org.raml.ramltopojo.extensions.jackson2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.squareup.javapoet.*;
import joptsimple.internal.Strings;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.Names;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class JacksonUnionExtension extends UnionTypeHandlerPlugin.Helper {

    @Override
    public ClassName className(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {
        return currentSuggestion;
    }

    @Override
    public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {

        ClassName deserializer =
                ClassName.get("", unionPluginContext.creationResult().getJavaName(EventType.INTERFACE).simpleName(),
                        Names.typeName(ramlType.name(), "deserializer"));

        ClassName serializer =
                ClassName.get("", unionPluginContext.creationResult().getJavaName(EventType.INTERFACE).simpleName(),
                        Names.typeName("serializer"));

        createSerializer(serializer, ramlType, incoming, eventType);
        createDeserializer(unionPluginContext, deserializer, ramlType, incoming, eventType);

        incoming.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
                .addMember("using", "$T.class", deserializer).build());
        incoming.addAnnotation(AnnotationSpec.builder(JsonSerialize.class)
                .addMember("using", "$T.class", serializer).build());

        return incoming;
    }

    private void createSerializer(ClassName serializerName, UnionTypeDeclaration union, TypeSpec.Builder typeBuilder, EventType eventType) {

        if ( eventType == EventType.IMPLEMENTATION) {
            return;
        }

        ClassName typeBuilderName = ClassName.get("", typeBuilder.build().name);
        TypeSpec.Builder builder = TypeSpec.classBuilder(serializerName)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(StdSerializer.class), typeBuilderName))
                .addMethod(
                        MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addCode("super($T.class);", typeBuilderName).build()

                ).addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder serialize = MethodSpec.methodBuilder("serialize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(typeBuilderName, "object").build())
                .addParameter(ParameterSpec.builder(ClassName.get(JsonGenerator.class), "jsonGenerator").build())
                .addParameter(ParameterSpec.builder(ClassName.get(SerializerProvider.class), "jsonSerializerProvider").build())
                .addException(IOException.class)
                .addException(JsonProcessingException.class);

        for (TypeDeclaration typeDeclaration : union.of()) {

            String isMethod = Names.methodName("is", typeDeclaration.name());
            String getMethod = Names.methodName("get", typeDeclaration.name());
            serialize.beginControlFlow("if ( object." + isMethod + "())");
            serialize.addStatement("jsonGenerator.writeObject(object." + getMethod + "())");
            serialize.addStatement("return");
            serialize.endControlFlow();
        }

        serialize.addStatement("throw new $T($S + object)", IOException.class, "Can't figure out type of object");

        builder.addMethod(serialize.build());
        typeBuilder.addType(builder.build());
    }

    private void createDeserializer(UnionPluginContext unionPluginContext, ClassName serializerName, UnionTypeDeclaration union, TypeSpec.Builder typeBuilder, EventType eventType) {

        if ( eventType == EventType.IMPLEMENTATION) {
            return;
        }

        ClassName typeBuilderName = ClassName.get("", typeBuilder.build().name);

        TypeSpec.Builder builder = TypeSpec.classBuilder(serializerName)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(StdDeserializer.class), typeBuilderName))
                .addMethod(
                        MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addCode("super($T.class);", typeBuilderName).build()

                ).addModifiers(Modifier.PUBLIC);

        MethodSpec.Builder deserialize = MethodSpec.methodBuilder("deserialize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(JsonParser.class), "jsonParser").build())
                .addParameter(ParameterSpec.builder(ClassName.get(DeserializationContext.class), "jsonContext").build())
                .addException(IOException.class)
                .addException(JsonProcessingException.class)
                .returns(typeBuilderName)
                .addStatement("$T mapper  = new $T()", ObjectMapper.class, ObjectMapper.class)
                .addStatement("$T<String, Object> map = mapper.readValue(jsonParser, Map.class)", Map.class);


        for (TypeDeclaration typeDeclaration : union.of()) {

            TypeName unionPossibility = unionPluginContext.unionClass(typeDeclaration).getJavaName(EventType.IMPLEMENTATION);

            String name = Names.methodName("looksLike", typeDeclaration.name());
            deserialize.addStatement("if ( " + name + "(map) ) return new $T(mapper.convertValue(map, $T.class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), unionPossibility);

            buildLooksLike(builder, typeDeclaration);
        }


        deserialize.addStatement("throw new $T($S + map)", IOException.class, "Can't figure out type of object");
        builder.addMethod(deserialize.build());

        typeBuilder.addType(builder.build());
    }

    private void buildLooksLike(TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

        String name = Names.methodName("looksLike", typeDeclaration.name());
        MethodSpec.Builder spec =
                MethodSpec.methodBuilder(name).addParameter(ParameterizedTypeName.get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(Object.class)), "map");
        if (typeDeclaration instanceof ObjectTypeDeclaration) {

            ObjectTypeDeclaration otd = (ObjectTypeDeclaration) typeDeclaration;
            List<String> names = Lists.transform(otd.properties(), new Function<TypeDeclaration, String>() {

                @Nullable
                @Override
                public String apply(@Nullable TypeDeclaration input) {
                    return "\"" + input.name() + "\"";
                }
            });

            spec.addStatement("return map.keySet().containsAll($T.asList($L))", Arrays.class, Strings.join(names, ","));
        }

        spec.addModifiers(Modifier.PRIVATE).returns(TypeName.BOOLEAN);
        builder.addMethod(spec.build());
    }

}
