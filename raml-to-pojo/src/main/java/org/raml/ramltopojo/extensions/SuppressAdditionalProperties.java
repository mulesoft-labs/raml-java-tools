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
import org.raml.ramltopojo.extensions.utils.DefaultTypeCopyHandler;
import org.raml.ramltopojo.extensions.utils.TypeCopier;

/**
 * Created. There, you have it.
 */
public class SuppressAdditionalProperties extends ObjectTypeHandlerPlugin.Helper {

  @Override
  public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {

    TypeCopier copier = new TypeCopier(new DefaultTypeCopyHandler() {

      @Override
      public boolean handleMethod(TypeSpec.Builder newType, MethodSpec methodSpec) {
        if (methodSpec.name.equals("setAdditionalProperties") || methodSpec.name.equals("getAdditionalProperties")) {
          return true;
        } else {

          return super.handleMethod(newType, methodSpec);
        }
      }
    });

    return copier.copy(incoming, "");
  }
}
