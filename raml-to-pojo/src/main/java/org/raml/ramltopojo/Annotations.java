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

    public static Annotations<Boolean> ABSTRACT = new Annotations<Boolean>() {

        @Override
        public Boolean getWithContext(Annotable target, Annotable... others) {

            return getWithDefault(new TypeInstanceAsBooleanFunction(), "abstract", false, target, others);
        }
    };

    public static Annotations<Boolean> USE_PRIMITIVE = new Annotations<Boolean>() {

        @Override
        public Boolean getWithContext(Annotable target, Annotable... others) {

            return getWithDefault(new TypeInstanceAsBooleanFunction(), "usePrimitiveType", true, target, others);
        }
    };

//    public static Annotations<Boolean> GENERATE_INLINE_TYPE = new Annotations<Boolean>() {
//
//        @Override
//        public Boolean getWithContext(Annotable target, Annotable... others) {
//
//            return getWithDefault(new TypeInstanceAsBooleanFunction(), "generateInlineType", true, target, others);
//        }
//    };

    public static Annotations<String> IMPLEMENTATION_CLASS_NAME = new Annotations<String>() {

        @Override
        public String getWithContext(Annotable target, Annotable... others) {

            return getWithDefault(new TypeInstanceAsBooleanFunction(), "implementationClassName", null, target, others);
        }
    };

    public static Annotations<String> CLASS_NAME = new Annotations<String>() {

        @Override
        public String getWithContext(Annotable target, Annotable... others) {

            return getWithDefault(new TypeInstanceAsBooleanFunction(), "className", null, target, others);
        }
    };


    public static Annotations<List<PluginDef>> PLUGINS = new Annotations<List<PluginDef>>() {

        @Override
        public List<PluginDef> getWithContext(Annotable target, Annotable... others) {
            return Annotations.getWithDefaultList(new TypeInstanceToPluginDefFunction(), "plugins", target, others);
        }
    };


    private static <T,R> R getWithDefault(Function<TypeInstance, T> convert, String propName, R def, Annotable target, Annotable... others) {
        R b = Annotations.evaluate(convert, "types", propName, target, others);
        if (b == null) {

            return def;
        } else {
            return b;
        }
    }

    private static <T,R> List<R> getWithDefaultList(Function<TypeInstance, T> convert, String propName, Annotable target, Annotable... others) {
        List<R> b = Annotations.evaluateAsList(convert, "types", propName, target, others);
        if (b == null) {

            return Collections.emptyList();
        } else {
            return b;
        }
    }

    public static <T,R> R evaluate(Function<TypeInstance, T> convert, String annotationName, String parameterName, Annotable mandatory, Annotable... others) {

        R retval = null;
        List<Annotable> targets = new ArrayList<>();
        targets.add(mandatory);
        targets.addAll(Arrays.asList(others));

        for (Annotable target : targets) {

            AnnotationRef annotationRef = Annotations.findRef(target, annotationName);
            if (annotationRef == null) {

                continue;
            }

            Object o = findProperty(annotationRef, parameterName, convert);
            if (o != null) {
                retval = (R) o;
            }

        }

        return retval;
    }

    public static <T,R> List<R> evaluateAsList(Function<TypeInstance, T> convert, String annotationName, String parameterName, Annotable mandatory, Annotable... others) {

        List<Annotable> targets = new ArrayList<>();
        targets.add(mandatory);
        targets.addAll(Arrays.asList(others));

        List<R> finalList = new ArrayList<>();

        for (Annotable target : targets) {

            AnnotationRef annotationRef = Annotations.findRef(target, annotationName);
            if (annotationRef == null) {

                continue;
            }

            Object o = findProperty(annotationRef, parameterName, convert);
            if (o != null) {
                finalList.addAll((List)o);
            }
        }

        return finalList;
    }

    private static <T> Object findProperty(AnnotationRef annotationRef, String propName, Function<TypeInstance, T> convert) {


        // annotationRef.structuredValue().properties().get(0).values().get(0).value()
        for (TypeInstanceProperty typeInstanceProperty : annotationRef.structuredValue().properties()) {
            if (typeInstanceProperty.name().equalsIgnoreCase(propName)) {
                if (typeInstanceProperty.isArray()) {
                    return toValueList(convert, typeInstanceProperty.values());
                } else {
                    return convert.apply(typeInstanceProperty.value());
                }
            }
        }

        return null;
    }

    private static <T> List<T> toValueList(final Function<TypeInstance, T> convert, List<TypeInstance> values) {

        return Lists.transform(values, new Function<TypeInstance, T>() {

            @Nullable
            @Override
            public T apply(@Nullable TypeInstance input) {
                return convert.apply(input);
            }
        });
    }

    public static AnnotationRef findRef(Annotable annotable, String annotation) {

        for (AnnotationRef annotationRef : annotable.annotations()) {
            if (annotationRef.annotation().name().equalsIgnoreCase(annotation)) {

                return annotationRef;
            }
        }

        return null;
    }

    public abstract T getWithContext(Annotable target, Annotable... others);

    public T getValueWithDefault(T def, Annotable annotable, Annotable... others) {

        T t = getWithContext(annotable, others);
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

    public T get(T def, Annotable type, Annotable... others) {

        return getValueWithDefault(def, type, others);
    }

    private static class TypeInstanceAsStringFunction implements Function<TypeInstance, String> {
        @Nullable
        @Override
        public String apply(@Nullable TypeInstance input) {
            return (String) input.value();
        }
    }

    private static class TypeInstanceAsBooleanFunction implements Function<TypeInstance, Boolean> {
        @Nullable
        @Override
        public Boolean apply(@Nullable TypeInstance input) {
            return (Boolean) input.value();
        }
    }

    private static class TypeInstanceToPluginDefFunction implements Function<TypeInstance, PluginDef> {

        @Override
        public PluginDef apply(@Nullable TypeInstance input) {

            if (input.properties().size() == 0) {

                return new PluginDef((String) input.value(), Collections.<String>emptyList());
            } else {

                if ( input.properties().size() == 1 ) {

                    return new PluginDef((String) input.properties().get(0).value().value(), Collections.<String>emptyList());
                } else {
                    return new PluginDef((String) input.properties().get(0).value().value(), Lists.transform(input.properties().get(1).values(), new Function<TypeInstance, String>() {
                                @Nullable
                                @Override
                                public String apply(@Nullable TypeInstance input) {
                                    return (String) input.value();
                                }
                            }
                    ));
                }
            }
        }
    }
}
