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

import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.ClassParserFactory;
import org.raml.pojotoraml.field.FieldClassParser;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import static org.raml.pojotoraml.util.AnnotationFinder.annotationFor;

/**
 * Created. There, you have it.
 */
public class PojoToRamlClassParserFactory implements ClassParserFactory {

  private final String topPackage;

  public PojoToRamlClassParserFactory(String topPackage) {
    this.topPackage = topPackage;
  }

  private static ClassParser instantiateClassParser(RamlGeneratorForClass ramlGeneratorForClass) {
    try {
      return ramlGeneratorForClass.generator().parser().newInstance();
    } catch (InstantiationException | IllegalAccessException e) {

      return null;
    }
  }

  @Override
  public ClassParser createParser(final Type clazz) {

    if ( !( clazz instanceof Class )) {

      return new FieldClassParser();
    }

    RamlGenerator generator = ((Class<?>)clazz).getAnnotation(RamlGenerator.class);

    ClassParser parser = null;
    if (generator != null) {
      try {
        parser = generator.parser().newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
      }
    }

    if (parser == null && topPackage != null) {

      RamlGenerators generators = annotationFor(Package.getPackage(topPackage), RamlGenerators.class);
      Optional<ClassParser> classParserOptional =
          Arrays.stream(generators.value()).filter(ramlGeneratorForClass -> ramlGeneratorForClass.forClass().equals(clazz))
                  .findFirst()
                  .map(PojoToRamlClassParserFactory::instantiateClassParser);

      return classParserOptional.orElse(new FieldClassParser());
    }

    return Optional.ofNullable(parser).orElse(new FieldClassParser());
  }
}
