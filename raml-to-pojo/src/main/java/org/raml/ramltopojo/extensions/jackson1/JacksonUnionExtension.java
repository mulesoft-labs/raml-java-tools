package org.raml.ramltopojo.extensions.jackson1;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.javapoet.*;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.util.StdDateFormat;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.GenerationException;
import org.raml.ramltopojo.Names;
import org.raml.ramltopojo.Utils;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.AnyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NullTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

        createSerializer(unionPluginContext, serializer, ramlType, incoming, eventType);
        createDeserializer(unionPluginContext, deserializer, ramlType, incoming, eventType);

        incoming.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
                .addMember("using", "$T.class", deserializer).build());
        incoming.addAnnotation(AnnotationSpec.builder(JsonSerialize.class)
                .addMember("using", "$T.class", serializer).build());

        return incoming;
    }

    private void createSerializer(UnionPluginContext unionPluginContext, ClassName serializerName, UnionTypeDeclaration union, TypeSpec.Builder typeBuilder, EventType eventType) {

        if ( eventType == EventType.IMPLEMENTATION) {
            return;
        }

        // check if union is ambiguous (duplicate primitive types)
        if (isAmbiguousUnion(union.of())) {
            throw new GenerationException(
                "This union is ambiguous. It's impossible to create a Jackson-Serialization/-Deserialization strategy for ambiguous types: "
                    + union.of().stream().map(x -> prettyName(x, unionPluginContext)).collect(Collectors.toList())
                    + ". Use unique primitive types or classes with discriminator to solve this conflict."
            );
        }

        ClassName typeBuilderName = ClassName.get("", typeBuilder.build().name);
        TypeSpec.Builder builder = TypeSpec.classBuilder(serializerName)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(SerializerBase.class), typeBuilderName))
                .addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super($T.class)", typeBuilderName)
                    .build());
        MethodSpec.Builder serialize = MethodSpec.methodBuilder("serialize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(typeBuilderName, "object").build())
                .addParameter(ParameterSpec.builder(ClassName.get(JsonGenerator.class), "jsonGenerator").build())
                .addParameter(ParameterSpec.builder(ClassName.get(SerializerProvider.class), "jsonSerializerProvider").build())
                .addException(IOException.class)
                .addException(JsonProcessingException.class);

        for (TypeDeclaration typeDeclaration : union.of()) {

            // use defined type name or primitives names
            String name = prettyName(typeDeclaration, unionPluginContext);

            String isMethod = Names.methodName("is", name);
            String getMethod = Names.methodName("get", name);
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
                .addStatement("$T node = mapper.readTree(jsonParser)", JsonNode.class);

        boolean dateValidation = false;
        boolean objectValidation = false;

        // we need to sort types for best deserialization results (int before number,
        // date before string, ...)
        List<TypeDeclaration> sortedTypes = new LinkedList<TypeDeclaration>(union.of());
        Map<Class<? extends TypeDeclaration>, Integer> typePriority = getPriorityTypeMap();
        Collections.sort(sortedTypes,(t1, t2) -> {
                // if both types are objects, we first do discriminator objects
                if (t1 instanceof ObjectTypeDeclaration && t2 instanceof ObjectTypeDeclaration) {
                    String d1 = ((ObjectTypeDeclaration) t1).discriminator();
                    String d2 = ((ObjectTypeDeclaration) t2).discriminator();
                    return d1 != null && d2 != null ? 0 : (d2 == null ? -1 : (d1 == null ? 1 : 0));
                }
                // no furhter process needed for other types
                return Integer.compare(typePriority.get(Utils.declarationType(t1)), typePriority.get(Utils.declarationType(t2)));
            }
        );

        for (TypeDeclaration typeDeclaration : sortedTypes) {

            // get type name of declaration
            TypeName typeName = unionPluginContext.findType(typeDeclaration.name(), typeDeclaration).box();

            if (typeDeclaration instanceof NullTypeDeclaration) {

                deserialize.beginControlFlow("if (node.isNull())");
                deserialize.addStatement("return new $T(null)", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof BooleanTypeDeclaration) {

                deserialize.beginControlFlow("if (node.isBoolean())");
                deserialize.addStatement("return new $T(node.asBoolean())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof IntegerTypeDeclaration) {

                if (typeName.box().equals(TypeName.LONG.box())) {
                    deserialize.beginControlFlow("if (node.isLong())");
                    deserialize.addStatement("return new $T(node.asLong())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                    deserialize.endControlFlow();
                }

                if (typeName.box().equals(TypeName.INT.box())) {
                    deserialize.beginControlFlow("if (node.isInt())");
                    deserialize.addStatement("return new $T(node.asInt())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                    deserialize.endControlFlow();
                }

                if (typeName.box().equals(TypeName.SHORT.box())) {
                    deserialize.beginControlFlow("if (node.isShort())");
                    deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class)", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), typeName);
                    deserialize.endControlFlow();
                }

            } else if (typeDeclaration instanceof StringTypeDeclaration) {

                deserialize.beginControlFlow("if (node.isTextual())");
                deserialize.addStatement("return new $T(node.asText())", unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION));
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof NumberTypeDeclaration) {

                deserialize.beginControlFlow("if (node.isNumber())");
                deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), Number.class);
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof DateTypeDeclaration) {

                dateValidation = true;

                deserialize.beginControlFlow("if (node.isTextual() && isValidDate(node.asText(), $T.getDateInstance()))", StdDateFormat.class);
                deserialize.addStatement("mapper.setDateFormat($T.getDateInstance())", StdDateFormat.class);
                deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), Date.class);
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof TimeOnlyTypeDeclaration) {

                dateValidation = true;

                deserialize.beginControlFlow("if (node.isTextual() && isValidDate(node.asText(), $T.getTimeInstance()))", StdDateFormat.class);
                deserialize.addStatement("mapper.setDateFormat($T.getTimeInstance())", StdDateFormat.class);
                deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), Date.class);
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof DateTimeOnlyTypeDeclaration) {

                dateValidation = true;

                deserialize.beginControlFlow("if (node.isTextual() && isValidDate(node.asText(), $T.getDateTimeInstance()))", StdDateFormat.class);
                deserialize.addStatement("mapper.setDateFormat($T.getDateTimeInstance())", StdDateFormat.class);
                deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION),Date.class);
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof DateTimeTypeDeclaration) {

                dateValidation = true;

                if (Objects.equals("rfc2616", ((DateTimeTypeDeclaration) typeDeclaration).format())) {
                    deserialize.beginControlFlow("if (node.isTextual() && isValidDate(node.asText()), new $T($S)))",
                        SimpleDateFormat.class, "EEE, dd MMM yyyy HH:mm:ss z");
                    deserialize.addStatement("mapper.setDateFormat(new $T($S))", SimpleDateFormat.class, "EEE, dd MMM yyyy HH:mm:ss z");
                    deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
                        unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), Date.class);
                    deserialize.endControlFlow();
                } else {
                    deserialize.beginControlFlow("if (node.isTextual() && isValidDate(node.asText()), $T.getDateTimeInstance()))", StdDateFormat.class);
                    deserialize.addStatement("mapper.setDateFormat($T.getDateTimeInstance())", StdDateFormat.class);
                    deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
                        unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), Date.class);
                    deserialize.endControlFlow();
                }

            } else if (typeDeclaration instanceof ArrayTypeDeclaration) {

                ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) typeDeclaration;
                TypeName arrayType = unionPluginContext.findType(arrayTypeDeclaration.name(), arrayTypeDeclaration).box();

                deserialize.beginControlFlow("if (node.isArray())");
                deserialize.addStatement("return new $T(mapper.treeToValue(node, $T[].class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), arrayType);
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof ObjectTypeDeclaration) {

                objectValidation = true;
                ObjectTypeDeclaration otd = (ObjectTypeDeclaration) typeDeclaration;

                List<String> names = Lists.transform(otd.properties(), new Function<TypeDeclaration, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable TypeDeclaration input) {
                        return "\"" + input.name() + "\"";
                    }
                });

                deserialize.beginControlFlow("if (node.isObject() && isValidObject(node, $T.asList($L)))", Arrays.class, Joiner.on(",").join(names));
                deserialize.addStatement("return new $T(mapper.treeToValue(node, $T.class))",
                    unionPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION), typeName);
                deserialize.endControlFlow();

            } else if (typeDeclaration instanceof AnyTypeDeclaration) {

                throw new GenerationException("Type 'any' within a union is not supported yet");

            } else if (typeDeclaration instanceof UnionTypeDeclaration) {

                throw new GenerationException("Type 'union' within a union is not supported yet");

            } else if (typeDeclaration instanceof FileTypeDeclaration) {

                throw new GenerationException("Type 'file' within a union is not supported yet");

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

    private Map<Class<? extends TypeDeclaration>, Integer> getPriorityTypeMap() {
        return new ImmutableMap.Builder<Class<? extends TypeDeclaration>, Integer>().put(NullTypeDeclaration.class, 1)
            .put(BooleanTypeDeclaration.class, 2)
            .put(IntegerTypeDeclaration.class, 3)
            .put(NumberTypeDeclaration.class, 4)
            .put(DateTypeDeclaration.class, 5)
            .put(TimeOnlyTypeDeclaration.class, 6)
            .put(DateTimeOnlyTypeDeclaration.class, 7)
            .put(DateTimeTypeDeclaration.class, 8)
            .put(StringTypeDeclaration.class, 9)
            .put(ObjectTypeDeclaration.class, 10)
            .put(ArrayTypeDeclaration.class, 11)
            .put(UnionTypeDeclaration.class, 12)
            .put(FileTypeDeclaration.class, 13)
            .put(AnyTypeDeclaration.class, 14)
            .build();
    }

    private void buildDateValidation(TypeSpec.Builder builder) {
        MethodSpec.Builder spec =
            MethodSpec.methodBuilder("isValidDate").addParameter(ClassName.get(String.class), "value").addParameter(ClassName.get(DateFormat.class), "format");
        spec.addStatement("try { return format.parse(value) != null; } catch ($T e) { return false; }", ParseException.class);
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

    private boolean isAmbiguousUnion(List<TypeDeclaration> typeDeclarations) {
        Set<Class<? extends TypeDeclaration>> uniqueTypeSet = new HashSet<>();
        for (TypeDeclaration typeDeclaration : typeDeclarations) {
            boolean isUnique = uniqueTypeSet.add(typeDeclaration.getClass());
            // we already have this type
            if (!isUnique) {
                // it's valid to have different object types
                if (typeDeclaration instanceof ObjectTypeDeclaration) {
                    continue;
                }
                // all primitive types are invalid => ambiguous
                return true;
            }
        }
        return false;
    }

    private String prettyName(TypeDeclaration type, UnionPluginContext unionPluginContext) {
        if (type.type() == null) {
            return type instanceof NullTypeDeclaration ? "nil" : shorten(unionPluginContext.findType(type.name(), type).box());
        } else {
            return type.name();
        }
    }

    private String shorten(TypeName typeName) {
        if (!(typeName instanceof ClassName)) {
            throw new GenerationException(typeName + toString() + " cannot be shortened reasonably");
        } else {
            return ((ClassName) typeName).simpleName();
        }
    }

}
