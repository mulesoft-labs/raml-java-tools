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
package org.raml.ramltopojo.extensions.tools;

import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;


/**
 * Created by Jean-Philippe Belanger on 3/15/17. Just potential zeroes and ones
 */
public class ChainSetter extends ObjectTypeHandlerPlugin.Helper {

  @Override
  public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder methodSpec, EventType eventType) {

    MethodSpec spec = methodSpec.build();
      MethodSpec seen = methodSpec.build();
      MethodSpec.Builder newBuilder = MethodSpec.methodBuilder("with" + spec.name.substring(3))
          .addModifiers(seen.modifiers)
          .returns(spec.parameters.get(0).type);

      commonStuffToCopy(seen, newBuilder);

      if (eventType == EventType.IMPLEMENTATION) {
        newBuilder.addStatement("this.$L = $L", spec.name.substring(3).toLowerCase(),  spec.name.substring(3).toLowerCase())
            .addStatement("return this");

        return newBuilder;
      }

      if (eventType == EventType.INTERFACE) {

        return newBuilder;
      }

      return methodSpec;

  }

  private void commonStuffToCopy(MethodSpec seen, MethodSpec.Builder newBuilder) {
    for (ParameterSpec parameter : seen.parameters) {
      newBuilder.addParameter(parameter);
    }

    for (AnnotationSpec annotation : seen.annotations) {
      newBuilder.addAnnotation(annotation);
    }
  }
}
