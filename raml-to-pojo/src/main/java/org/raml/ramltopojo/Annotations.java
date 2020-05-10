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
package org.raml.ramltopojo;

import amf.client.model.Annotable;
import amf.client.model.domain.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jean-Philippe Belanger on 1/2/17. Just potential zeroes and ones
 */
public abstract class Annotations<T> implements AnnotationUser<T> {


    public static Annotations<List<PluginDef>> PLUGINS = new Annotations<List<PluginDef>>() {

        @Override
        public List<PluginDef> getWithContext(Annotable target, Annotable... others) {
            return AnnotationEngine.getWithDefaultList("ramltopojo.plugins", Annotations::mapToPluginDefs, target, others);
        }
    };

    private static List<PluginDef> mapToPluginDefs(ArrayNode arrayNode) {
        return Optional.ofNullable(arrayNode).orElse(new ArrayNode()).members().stream()
                .filter(n -> n instanceof ObjectNode)
                .map(n -> (ObjectNode) n)
                .map(on -> new PluginDef(
                        ((ScalarNode)on.properties().get("name")).value().value(),
                        Optional.ofNullable((ArrayNode) on.properties().get("arguments")).orElseGet(ArrayNode::new).members().stream()
                                .filter( o -> o instanceof ScalarNode)
                                .map(o -> ((ScalarNode)o).value().value())
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
