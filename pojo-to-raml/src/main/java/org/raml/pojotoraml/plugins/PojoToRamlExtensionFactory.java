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
package org.raml.pojotoraml.plugins;

//import com.google.common.base.Function;

import com.google.common.collect.Streams;
import org.raml.pojotoraml.RamlAdjuster;
import org.raml.ramltopojo.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.raml.pojotoraml.util.AnnotationFinder.annotationFor;

/**
 * Created. There, you have it.
 */
public class PojoToRamlExtensionFactory {

    private static PluginManager pluginManager = PluginManager.createPluginManager("META-INF/pojotoraml-plugin.properties");

    private final String topPackage;

    private static final Logger logger = LoggerFactory.getLogger(PojoToRamlExtensionFactory.class);

    public PojoToRamlExtensionFactory(String topPackage) {
        this.topPackage = topPackage;
    }

    public RamlAdjuster createAdjusters(final Type clazz, final RamlAdjuster... ramlAdjusters) {

        if (!(clazz instanceof Class)) {

            return new RamlAdjuster.Composite(Arrays.asList(ramlAdjusters));
        }

        RamlGenerator generator = ((Class<?>) clazz).getAnnotation(RamlGenerator.class);
        if (generator != null) {
            return new RamlAdjuster.Composite(
                    Streams.concat(Arrays.stream(generator.plugins())
                            .map(ramlGeneratorPlugin -> new RamlAdjuster.Composite(pluginManager.getClassesForName(
                                    ramlGeneratorPlugin.plugin(),
                                    Arrays.asList(ramlGeneratorPlugin.parameters()), RamlAdjuster.class))), Arrays.stream(ramlAdjusters)).collect(Collectors.toList()));
        } else {

            return withNoLocalAnnotation(clazz, ramlAdjusters);
        }

    }

    private RamlAdjuster withNoLocalAnnotation(Type clazz, RamlAdjuster[] ramlAdjusters) {
        if (topPackage != null) {
            RamlGenerators generators = annotationFor(Package.getPackage(topPackage), RamlGenerators.class);
            logger.debug("{} RamlGenerators: {} '{}'\n", "******* ", generators, " *******");
            // get the generator for the class.
            java.util.Optional<RamlGenerator> ramlAdjusterOptional =
                    Arrays.stream(generators.value()).filter(ramlGeneratorForClass -> ramlGeneratorForClass.forClass().equals(clazz))
                            .findFirst()
                            .map(RamlGeneratorForClass::generator);

            java.util.Optional<RamlAdjuster> finalAdjuster = ramlAdjusterOptional.map(new Function<RamlGenerator, RamlAdjuster>() {

                @Nullable
                @Override
                public RamlAdjuster apply(RamlGenerator ramlGenerator) {
                    return new RamlAdjuster.Composite(Streams.concat(
                            Arrays.stream(ramlGenerator.plugins())
                                    .map(ramlGeneratorPlugin -> new RamlAdjuster.Composite(pluginManager.getClassesForName(ramlGeneratorPlugin.plugin(),
                                            Arrays.asList(ramlGeneratorPlugin.parameters()), RamlAdjuster.class))), Arrays.stream(ramlAdjusters)).collect(Collectors.toList()));
                }
            });

            return finalAdjuster.orElseGet(() -> new RamlAdjuster.Composite(Arrays.asList(ramlAdjusters)));
        } else {

            return new RamlAdjuster.Composite(Arrays.asList(ramlAdjusters));
        }
    }
}
