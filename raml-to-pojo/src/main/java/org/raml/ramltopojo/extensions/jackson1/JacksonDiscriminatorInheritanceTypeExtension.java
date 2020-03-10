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
package org.raml.ramltopojo.extensions.jackson1;

import amf.client.model.domain.NodeShape;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

/**
 * Created by Jean-Philippe Belanger on 1/1/17. Just potential zeroes and ones
 */
public class JacksonDiscriminatorInheritanceTypeExtension extends ObjectTypeHandlerPlugin.Helper {

  @Override
  public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder typeSpec, EventType eventType) {

    if ( eventType == EventType.IMPLEMENTATION) {
      return typeSpec;
    }

    if (!ramlType.discriminator().isNullOrEmpty() && objectPluginContext.childClasses(ramlType.name().value()).size() > 0) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeInfo.class)
              .addMember("use", "$T.Id.NAME", JsonTypeInfo.class)
              .addMember("include", "$T.As.EXISTING_PROPERTY", JsonTypeInfo.class)
              .addMember("property", "$S", ramlType.discriminator()).build());

      AnnotationSpec.Builder subTypes = AnnotationSpec.builder(JsonSubTypes.class);
      for (CreationResult result : objectPluginContext.childClasses(ramlType.name().value())) {

        subTypes.addMember(
                "value",
                "$L",
                AnnotationSpec
                        .builder(JsonSubTypes.Type.class)
                        .addMember("value", "$L",
                                result.getJavaName(EventType.INTERFACE) + ".class").build());
      }

      subTypes.addMember(
              "value",
              "$L",
              AnnotationSpec
                      .builder(JsonSubTypes.Type.class)
                      .addMember("value", "$L",
                              objectPluginContext.creationResult().getJavaName(EventType.INTERFACE) + ".class").build());

      typeSpec.addAnnotation(subTypes.build());

    }

    if (!ramlType.discriminatorValue().isNullOrEmpty()) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeName.class)
              .addMember("value", "$S", ramlType.discriminatorValue()).build());
    }


    typeSpec.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
            .addMember("as", "$T.class", objectPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION))
            .build());


    return typeSpec;
  }

}
