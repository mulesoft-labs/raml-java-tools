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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/2/17. Just potential zeroes and ones
 */
public abstract class Annotations<T> {


  public static Annotations<String> CLASS_NAME = new Annotations<String>() {

    @Override
    public String getWithContext(Annotable target, Annotable... others) {

      return getWithDefault("classname", null, target, others);
    }
  };

  public static Annotations<String> IMPLEMENTATION_CLASS_NAME = new Annotations<String>() {

    @Override
    public String getWithContext(Annotable target, Annotable... others) {

      return getWithDefault("implementationClassName", null, target, others);
    }
  };

  public static Annotations<Boolean> USE_PRIMITIVE_TYPE = new Annotations<Boolean>() {

    @Override
    public Boolean getWithContext(Annotable target, Annotable... others) {

      return getWithDefault("usePrimitiveType", false, target, others);
    }

  };

  public static Annotations<Boolean> ABSTRACT = new Annotations<Boolean>() {

    @Override
    public Boolean getWithContext(Annotable target, Annotable... others) {

      return getWithDefault("abstract", false, target, others);
    }
  };


  public static Annotations<List<String>> PLUGINS = new Annotations<List<String>>() {

    @Override
    public List<String> getWithContext(Annotable target, Annotable... others) {
      return Annotations.getWithDefault("plugins", Collections.<String>emptyList(), target, others);
    }
  };

/*
  */
/*
   * Types
   *//*

  public static Annotations<TypeExtension> ON_TYPE_CLASS_CREATION = new Annotations<TypeExtension>() {

    @Override
    public TypeExtension getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      List<String> classNames = getWithDefault("types", "onTypeCreation", null, target, others);

      List<TypeExtension> extension = createExtension(currentBuild, classNames);
      return new TypeExtension.Composite(extension);
    }
  };

  public static Annotations<TypeExtension> ON_TYPE_CLASS_FINISH = new Annotations<TypeExtension>() {

    @Override
    public TypeExtension getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      List<String> classNames = getWithDefault("types", "onTypeFinish", null, target, others);

      List<TypeExtension> extension = createExtension(currentBuild, classNames);
      return new TypeExtension.Composite(extension);
    }
  };

  public static Annotations<FieldExtension> ON_TYPE_FIELD_CREATION = new Annotations<FieldExtension>() {

    @Override
    public FieldExtension getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      List<String> classNames = getWithDefault("types", "onFieldCreation", null, target, others);
      List<FieldExtension> extensions = createExtension(currentBuild, classNames);

      return new FieldExtension.Composite(extensions);
    }
  };

  public static Annotations<MethodExtension> ON_TYPE_METHOD_CREATION = new Annotations<MethodExtension>() {

    @Override
    public MethodExtension getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      List<String> classNames = getWithDefault("types", "onMethodCreation", null, target, others);
      List<MethodExtension> extension = createExtension(currentBuild, classNames);
      return new MethodExtension.Composite(extension);
    }
  };
*/



  private static <T> T getWithDefault(String propName, T def, Annotable target, Annotable... others) {
    T b = Annotations.evaluate(propName, target, others);
    if (b == null) {

      return def;
    } else {
      return b;
    }
  }

  private static <T> T evaluate(String parameterName, Annotable mandatory, Annotable... others) {

    T retval = null;
    List<Annotable> targets = new ArrayList<>();
    targets.add(mandatory);
    targets.addAll(Arrays.asList(others));

    for (Annotable target : targets) {

      AnnotationRef annotationRef = Annotations.findRef(target, "types");
      if (annotationRef == null) {

        continue;
      }

      Object o = findProperty(annotationRef, parameterName);
      if (o != null) {
        retval = (T) o;
      }

    }

    return retval;
  }

  private static Object findProperty(AnnotationRef annotationRef, String propName) {


    // annotationRef.structuredValue().properties().get(0).values().get(0).value()
    for (TypeInstanceProperty typeInstanceProperty : annotationRef.structuredValue().properties()) {
      if (typeInstanceProperty.name().equalsIgnoreCase(propName)) {
        if (typeInstanceProperty.isArray()) {
          return toValueList(typeInstanceProperty.values());
        } else {
          return typeInstanceProperty.value().value();
        }
      }
    }

    return null;
  }

  private static List<Object> toValueList(List<TypeInstance> values) {

    return Lists.transform(values, new Function<TypeInstance, Object>() {

      @Nullable
      @Override
      public Object apply(@Nullable TypeInstance input) {
        return input.value();
      }
    });
  }

  private static AnnotationRef findRef(Annotable annotable, String annotation) {

    for (AnnotationRef annotationRef : annotable.annotations()) {
      if (annotationRef.annotation().name().equalsIgnoreCase(annotation)) {

        return annotationRef;
      }
    }

    return null;
  }

  public abstract T getWithContext(Annotable target, Annotable... others);

  public T getValueWithDefault(T def, Annotable annotable, Annotable... others) {

    T t = getWithContext( annotable, others);
    if (t == null) {

      return def;
    } else {
      return t;
    }
  }

  public T get(T def, Annotable type) {

    return getValueWithDefault(def, type);
  }

  public T get(Annotable type) {

    return getValueWithDefault(null, type);
  }

  public T get(T def, Annotable type, Annotable others) {

    return getValueWithDefault(def, type, others);
  }

}
