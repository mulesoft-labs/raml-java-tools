package org.raml.ramltopojo.extensions.jackson2;

import amf.client.model.domain.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationException;
import org.raml.ramltopojo.Names;
import org.raml.ramltopojo.ScalarTypes;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;
import org.raml.ramltopojo.union.UnionTypesHelper;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created. There, you have it.
 */
public class JacksonUnionExtension extends UnionTypeHandlerPlugin.Helper {

    @Override
    public ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType) {
        return currentSuggestion;
    }

    @Override
    public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionShape ramlType, TypeSpec.Builder incoming, EventType eventType) {

        ClassName deserializer = ClassName.get("", unionPluginContext.creationResult().getJavaName(EventType.INTERFACE).simpleName(), Names.typeName("deserializer"));
        ClassName serializer = ClassName.get("", unionPluginContext.creationResult().getJavaName(EventType.INTERFACE).simpleName(), Names.typeName("serializer"));

        createSerializer(unionPluginContext, serializer, ramlType, incoming, eventType);
        createDeserializer(unionPluginContext, deserializer, ramlType, incoming, eventType);

        incoming.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class).addMember("using", "$T.class", deserializer).build());
        incoming.addAnnotation(AnnotationSpec.builder(JsonSerialize.class).addMember("using", "$T.class", serializer).build());

        return incoming;
    }

    private void createSerializer(UnionPluginContext unionPluginContext, ClassName serializerName, UnionShape union, TypeSpec.Builder typeBuilder, EventType eventType) {

        if (eventType == EventType.IMPLEMENTATION) {
            return;
        }

        ClassName typeBuilderName = ClassName.get("", typeBuilder.build().name);
        TypeSpec.Builder builder =
            TypeSpec.classBuilder(serializerName)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(StdSerializer.class), typeBuilderName))
                .addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super($T.class)", typeBuilderName)
                    .build())
                .addField(FieldSpec.builder(TypeName.LONG, "serialVersionUID")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                    .initializer("1L")
                    .build());

        MethodSpec.Builder serialize =
            MethodSpec.methodBuilder("serialize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(typeBuilderName, "object").build())
                .addParameter(ParameterSpec.builder(ClassName.get(JsonGenerator.class), "jsonGenerator").build())
                .addParameter(ParameterSpec.builder(ClassName.get(SerializerProvider.class), "jsonSerializerProvider").build())
                .addException(IOException.class)
                .addException(JsonProcessingException.class);

        for (Shape typeDeclaration : union.anyOf()) {

            // use defined type name or primitives names
            String name = prettyName((AnyShape) typeDeclaration, unionPluginContext);

            String isMethod = Names.methodName("is", name);
            String getMethod = Names.methodName("get", name);
            serialize.beginControlFlow("if ( object." + isMethod + "())");

            // Check for dates (special serialization)
            if (ScalarTypes.isDateOnly(typeDeclaration)) {

                serialize.addStatement("new $T().setDateFormat(new $T($S)).writeValue(jsonGenerator, object." + getMethod + "())", ObjectMapper.class, SimpleDateFormat.class, "yyyy-mm-dd");

            } else if (ScalarTypes.isTimeOnly(typeDeclaration)) {

                serialize.addStatement("new $T().setDateFormat(new $T($S)).writeValue(jsonGenerator, object." + getMethod + "())", ObjectMapper.class, SimpleDateFormat.class, "hh:mm:ss");

            } else if (ScalarTypes.isDatetimeOnly(typeDeclaration)) {

                serialize.addStatement("new $T().setDateFormat(new $T($S)).writeValue(jsonGenerator, object." + getMethod + "())", ObjectMapper.class, SimpleDateFormat.class, "yyyy-MM-dd'T'HH:mm:ss");

            } else if (ScalarTypes.isDateTime(typeDeclaration)) {

                if (Objects.equals("rfc2616", ((ScalarShape) typeDeclaration).format().value())) {
                    serialize.addStatement("new $T().setDateFormat(new $T($S)).writeValue(jsonGenerator, object." + getMethod + "())", ObjectMapper.class, SimpleDateFormat.class, "EEE, dd MMM yyyy HH:mm:ss z");
                } else {
                    serialize.addStatement("new $T().setDateFormat(new $T($S)).writeValue(jsonGenerator, object." + getMethod + "())", ObjectMapper.class, SimpleDateFormat.class, "yyyy-MM-dd'T'HH:mm:ssZ");
                }

            } else {

                serialize.addStatement("jsonGenerator.writeObject(object." + getMethod + "())");

            }

            serialize.addStatement("return");
            serialize.endControlFlow();
        }

        serialize.addStatement("throw new $T($S + object)", IOException.class, "Can't figure out type of object");

        builder.addMethod(serialize.build());
        typeBuilder.addType(builder.build());
    }

    private void createDeserializer(UnionPluginContext unionPluginContext, ClassName serializerName, UnionShape union, TypeSpec.Builder typeBuilder, EventType eventType) {

        if (eventType == EventType.IMPLEMENTATION) {
            return;
        }

        ClassName typeBuilderName = ClassName.get("", typeBuilder.build().name);

        TypeSpec.Builder builder =
            TypeSpec.classBuilder(serializerName)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(StdDeserializer.class), typeBuilderName))
                .addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super($T.class)", typeBuilderName)
                    .build())
                .addField(FieldSpec.builder(TypeName.LONG, "serialVersionUID")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                    .initializer("1L")
                    .build());

        MethodSpec.Builder deserialize =
            MethodSpec.methodBuilder("deserialize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(JsonParser.class), "jp").build())
                .addParameter(ParameterSpec.builder(ClassName.get(DeserializationContext.class), "jsonContext").build())
                .addException(IOException.class)
                .addException(JsonProcessingException.class)
                .returns(typeBuilderName)
                .addStatement("$T node = jp.getCodec().readTree(jp)", JsonNode.class);

        boolean dateValidation = false;
        boolean objectValidation = false;
        boolean nullMethod = false;

        for (AnyShape typeDeclaration : UnionTypesHelper.sortByPriority(union.anyOf())) {

            // get type name of declaration
            TypeName typeName = unionPluginContext.findType(typeDeclaration.name().value(), typeDeclaration).box();

            if (typeDeclaration instanceof NilShape) {

                deserialize.beginControlFlow("if (node.isNull())");
                deserialize.addStatement("return new $T(null)", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                deserialize.endControlFlow();

                if (!nullMethod) {
                    builder.addMethod(MethodSpec.methodBuilder("getNullValue")
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(ParameterSpec.builder(ClassName.get(DeserializationContext.class), "deserializationContext").build())
                            .returns(typeBuilderName)
                            .addStatement("return new $T(null)", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION))
                            .build());
                    nullMethod = true;
                }

            } else if (ScalarTypes.isBoolean(typeDeclaration)) {

                deserialize.beginControlFlow("if (node.isBoolean())");
                deserialize.addStatement("return new $T(node.asBoolean())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                deserialize.endControlFlow();

            } else if (ScalarTypes.isInteger(typeDeclaration)) {

                if (typeName.box().equals(TypeName.LONG.box())) {
                    deserialize.beginControlFlow("if (node.isLong())");
                    deserialize.addStatement("return new $T(node.asLong())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                    deserialize.endControlFlow();
                } else if (typeName.box().equals(TypeName.INT.box())) {
                    deserialize.beginControlFlow("if (node.isInt())");
                    deserialize.addStatement("return new $T(node.asInt())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                    deserialize.endControlFlow();
                } else if (typeName.box().equals(TypeName.SHORT.box())) {
                    deserialize.beginControlFlow("if (node.isShort())");
                    deserialize.addStatement("return new $T(jp.getCodec().treeToValue(node, $T.class)", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), typeName);
                    deserialize.endControlFlow();
                } else {
                    throw new GenerationException("Unknown integer type");
                }

            } else if (ScalarTypes.isString(typeDeclaration)) {

                deserialize.beginControlFlow("if (node.isTextual())");
                deserialize.addStatement("return new $T(node.asText())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                deserialize.endControlFlow();

            } else if (ScalarTypes.isNumber(typeDeclaration)) {

                deserialize.beginControlFlow("if (node.isNumber())");
                deserialize.addStatement("return new $T(jp.getCodec().treeToValue(node, $T.class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), Number.class);
                deserialize.endControlFlow();

            } else if (ScalarTypes.isDateOnly(typeDeclaration)) {

                dateValidation = true;
                this.buildDateDeserialize(unionPluginContext, deserialize, "yyyy-mm-dd");

            } else if (ScalarTypes.isTimeOnly(typeDeclaration)) {

                dateValidation = true;
                this.buildDateDeserialize(unionPluginContext, deserialize, "hh:mm:ss");

            } else if (ScalarTypes.isDatetimeOnly(typeDeclaration)) {

                dateValidation = true;
                this.buildDateDeserialize(unionPluginContext, deserialize, "yyyy-MM-dd'T'HH:mm:ss");

            } else if (ScalarTypes.isDateTime(typeDeclaration)) {

                dateValidation = true;

                if (Objects.equals("rfc2616", ((ScalarShape) typeDeclaration).format().value())) {

                    this.buildDateDeserialize(unionPluginContext, deserialize, "EEE, dd MMM yyyy HH:mm:ss z");

                } else {

                    this.buildDateDeserialize(unionPluginContext, deserialize, "yyyy-MM-dd'T'HH:mm:ssZ");

                }

            } else if (typeDeclaration instanceof ArrayShape) {

                ArrayShape arrayTypeDeclaration = (ArrayShape) typeDeclaration;
                TypeName arrayType = unionPluginContext.findType(arrayTypeDeclaration.name().value(), arrayTypeDeclaration).box();

                deserialize.beginControlFlow("if (node.isArray())");
                deserialize.addStatement("return new $T(jp.getCodec().treeToValue(node, $T[].class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), arrayType);
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof NodeShape) {

                objectValidation = true;
                NodeShape otd = (NodeShape) typeDeclaration;

                List<String> names = Lists.transform(otd.properties(), new Function<Shape, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Shape input) {
                        return "\"" + input.name().value() + "\"";
                    }
                });

                if (otd.discriminator().nonNull()) {

                    TypeName unionPossibility = unionPluginContext.unionClass(typeDeclaration).getJavaName(EventType.INTERFACE);

                    deserialize.beginControlFlow("if (node.isObject() && isValidObject(node, $T.asList($L)) && $T.equals(node.path($S).asText(), $S))",
                            Arrays.class,
                            Joiner.on(",").join(names),
                            Objects.class,
                            otd.discriminator(),
                            Optional.ofNullable(otd.discriminatorValue().value()).orElse(otd.name().value()));
                    deserialize.addStatement("return new $T(($T)jp.getCodec().treeToValue(node, $T.class))",
                        unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), unionPossibility, unionPluginContext.unionClass((AnyShape) findParentType(typeDeclaration)).getJavaName(EventType.INTERFACE));
                    deserialize.endControlFlow();

                } else {

                    deserialize.beginControlFlow("if (node.isObject() && isValidObject(node, $T.asList($L)))",
                            Arrays.class,
                            Joiner.on(",").join(names));
                    deserialize.addStatement("return new $T(jp.getCodec().treeToValue(node, $T.class))",
                        unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), typeName);
                    deserialize.endControlFlow();

                }

            } else if (typeDeclaration instanceof FileShape) {

                throw new GenerationException("Type 'file' within a union is not supported yet");

            } else if (typeDeclaration instanceof UnionShape) {

                throw new GenerationException("Type 'union' within a union is not supported yet");

            } else if (typeDeclaration instanceof AnyShape) {

                throw new GenerationException("Type 'any' within a union is not supported yet");

            } else {

                throw new GenerationException("Type 'unkown' within a union is not supported yet");

            }
        }

        if (dateValidation) {
            buildDateValidation(builder);
        }
        if (objectValidation) {
            buildObjectValidation(builder);
        }

        deserialize.addStatement("throw new $T($S + node)", IOException.class, "Can't figure out type of object ");
        builder.addMethod(deserialize.build());

        typeBuilder.addType(builder.build());
    }

    private Shape findParentType(Shape typeDeclaration) {
        if (typeDeclaration instanceof NodeShape) {
            NodeShape otd = (NodeShape) typeDeclaration;
            return otd.inherits().size() > 0 ? otd.inherits().get(0) : otd;
        } else {
            return typeDeclaration;
        }
    }

    private void buildDateDeserialize(UnionPluginContext unionPluginContext, MethodSpec.Builder deserialize, String dateFormat) {
        deserialize.beginControlFlow("if (node.isTextual() && isValidDate(node.asText(), new $T($S)))", SimpleDateFormat.class, dateFormat);
        deserialize.addStatement("$T mapper = new $T()", ObjectMapper.class, ObjectMapper.class);
        deserialize.addStatement("mapper.setDateFormat(new $T($S))", SimpleDateFormat.class, dateFormat);
        deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
            unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), Date.class);
        deserialize.endControlFlow();
    }

    private void buildDateValidation(TypeSpec.Builder builder) {
        MethodSpec.Builder spec =
            MethodSpec.methodBuilder("isValidDate").addParameter(ClassName.get(String.class), "value").addParameter(ClassName.get(DateFormat.class), "format");
        spec.beginControlFlow("try");
        spec.addStatement("return format.parse(value) != null");
        spec.endControlFlow();
        spec.beginControlFlow("catch ($T e)", ParseException.class);
        spec.addStatement("return false");
        spec.endControlFlow();
        spec.addModifiers(Modifier.PRIVATE).returns(TypeName.BOOLEAN);
        builder.addMethod(spec.build());
    }

    private void buildObjectValidation(TypeSpec.Builder builder) {
        MethodSpec.Builder spec =
            MethodSpec.methodBuilder("isValidObject")
                .addParameter(ClassName.get(JsonNode.class), "node")
                .addParameter(ParameterizedTypeName.get(List.class, String.class), "keys");
        spec.addStatement("$T<$T> list = new $T<>()", List.class, String.class, ArrayList.class);
        spec.addStatement("$T<$T> fieldIterator = node.fieldNames()", Iterator.class, String.class);
        spec.addStatement("while (fieldIterator.hasNext()) { list.add(fieldIterator.next()); }");
        spec.addStatement("return list.containsAll(keys)");
        spec.addModifiers(Modifier.PRIVATE).returns(TypeName.BOOLEAN);
        builder.addMethod(spec.build());
    }

    private String prettyName(AnyShape type, UnionPluginContext generationContext) {
        return type instanceof NilShape ? "nil" : type.name().isNullOrEmpty() ?shorten(generationContext.findType(type.name().value(), (AnyShape) type).box()):type.name().value();
    }

    private String shorten(TypeName typeName) {
        if (!(typeName instanceof ClassName)) {
            throw new GenerationException(typeName + toString() + " cannot be shortened reasonably");
        } else {
            return ((ClassName) typeName).simpleName();
        }
    }
}
