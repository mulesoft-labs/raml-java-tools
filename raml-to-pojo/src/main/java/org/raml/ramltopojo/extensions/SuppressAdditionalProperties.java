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
package org.raml.ramltopojo.extensions;

import amf.client.model.domain.NodeShape;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.javapoet.TypeSpecModifier;

/**
 * Created. There, you have it.
 */
public class SuppressAdditionalProperties extends ObjectTypeHandlerPlugin.Helper {

  @Override
  public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {

    TypeSpecModifier.modify(incoming)
            .modifyMethods(this::supressAdditionalProperties).modifyAll();

    return incoming;
  }

  private MethodSpec supressAdditionalProperties(MethodSpec methodSpec) {
    if (methodSpec.name.equals("setAdditionalProperties") || methodSpec.name.equals("getAdditionalProperties")) {
      return null;
    } else {

      return methodSpec;
    }
  }
}
