/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.ramltopojo.extensions.jackson2;

import amf.client.model.StrField;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.ScalarShape;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Created by Jean-Philippe Belanger on 1/8/17. Just potential zeroes and ones
 */
public class JacksonScalarTypeSerialization extends ObjectTypeHandlerPlugin.Helper {

  @Override
  public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape typeDeclaration, FieldSpec.Builder builder, EventType eventType) {

    if (typeDeclaration.range() instanceof ScalarShape) {
      ScalarShape propertyType = (ScalarShape) typeDeclaration.range();

      if ("datetime-only".equals(propertyType.dataType().value())) {

        builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
                .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
                .addMember("pattern", "$S", "yyyy-MM-dd'T'HH:mm:ss").build());
      }

      if ("time-only".equals(propertyType.dataType().value())) {

        builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
                .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
                .addMember("pattern", "$S", "HH:mm:ss").build());
      }

      if ("date".equals(propertyType.dataType().value())) {

        builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
                .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
                .addMember("pattern", "$S", "yyyy-MM-dd").build());
      }

      if ("datetime".equals(propertyType.dataType().value())) {

        // TODO:  do better
        Optional<String> format = Optional.ofNullable(propertyType.format()).map(StrField::value);
        if (format.isPresent() && "rfc2616".equals(format.get())) {

          builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
                  .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
                  .addMember("pattern", "$S", "EEE, dd MMM yyyy HH:mm:ss z").build());
        } else {
          TypeName name = objectPluginContext.createSupportClass(createSerialisationForDateTime(objectPluginContext));
          builder.addAnnotation(AnnotationSpec.builder(JsonFormat.class)
                  .addMember("shape", "$T.STRING", JsonFormat.Shape.class)
                  .addMember("pattern", "$S", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX").build())
                  .addAnnotation(AnnotationSpec.builder(JsonDeserialize.class).addMember("using", "$T.class", name).build());
        }
      }
    }
    return builder;
  }

  private TypeSpec.Builder createSerialisationForDateTime(ObjectPluginContext objectPluginContext) {

    ClassName returnType = ClassName.get(Date.class);
    TypeSpec.Builder builder = TypeSpec.classBuilder("TimestampDeserializer")
            .addModifiers(Modifier.PUBLIC)
            .superclass(ParameterizedTypeName.get(ClassName.get(StdDeserializer.class), returnType))
            .addField(FieldSpec.builder(StdDateFormat.class, "DATE_PARSER", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("new $T()", StdDateFormat.class).build())
            .addMethod(
                    MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addCode("super($T.class);", returnType).build()

            ).addModifiers(Modifier.PUBLIC);


    MethodSpec.Builder deserialize = MethodSpec.methodBuilder("deserialize")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(ClassName.get(JsonParser.class), "jsonParser").build())
            .addParameter(ParameterSpec.builder(ClassName.get(DeserializationContext.class), "jsonContext").build())
            .addException(IOException.class)
            .addException(JsonProcessingException.class)
            .returns(returnType)
            .addCode(
                    CodeBlock.builder().beginControlFlow("try").add(
                    CodeBlock.builder()
                      .addStatement("$T mapper  = new $T()", ObjectMapper.class, ObjectMapper.class)
                      .addStatement("$T dateString = mapper.readValue(jsonParser, String.class)", String.class)
                      .addStatement("Date date = DATE_PARSER.parse(dateString)", SimpleDateFormat.class)
                      .addStatement("return date").build()
                    ).add("} catch ($T e) {", ParseException.class).addStatement("throw new $T(e)", IOException.class).endControlFlow().build()
            );

    builder.addMethod(deserialize.build());
    objectPluginContext.createSupportClass(builder);
    return builder;
  }


/*
  @Override
  public void onEnumConstant(CurrentBuild currentBuild, TypeSpec.Builder builder,
                             TypeDeclaration typeDeclaration, String name) {


    builder.addAnnotation(AnnotationSpec.builder(JsonProperty.class).addMember("value", "$S", name)
        .build());
  }
*/
}
