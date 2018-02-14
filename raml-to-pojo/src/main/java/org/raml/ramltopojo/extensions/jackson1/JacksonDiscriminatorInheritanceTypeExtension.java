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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.raml.ramltopojo.Annotations;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/1/17. Just potential zeroes and ones
 */
public class JacksonDiscriminatorInheritanceTypeExtension extends ObjectTypeHandlerPlugin.Helper {

  @Override
  public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, TypeSpec.Builder typeSpec, EventType eventType) {

    if ( eventType == EventType.IMPLEMENTATION) {
      return typeSpec;
    }

    ObjectTypeDeclaration otr = ramlType;

    if (otr.discriminator() != null && objectPluginContext.childClasses(otr.name()).size() > 0) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeInfo.class)
              .addMember("use", "$T.Id.NAME", JsonTypeInfo.class)
              .addMember("include", "$T.As.PROPERTY", JsonTypeInfo.class)
              .addMember("property", "$S", otr.discriminator()).build());

      AnnotationSpec.Builder subTypes = AnnotationSpec.builder(JsonSubTypes.class);
      for (CreationResult result : objectPluginContext.childClasses(ramlType.name())) {

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

    if (otr.discriminatorValue() != null) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeName.class)
              .addMember("value", "$S", otr.discriminatorValue()).build());
    }


    if (!Annotations.ABSTRACT.get(otr)) {

      typeSpec.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
              .addMember("as", "$T.class", objectPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION))
              .build());
    }


    return typeSpec;
  }

}
