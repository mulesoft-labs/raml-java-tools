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
package org.raml.pojotoraml.types;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.raml.builder.PropertyValueBuilder;
import org.raml.builder.TypeBuilder;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ScalarType implements RamlType {
    NUMBER("number", new ImmutableMap.Builder<Class<?>, String>()
            .put(float.class, "float")
            .put(Float.class, "float")
            .put(double.class, "double")
            .put(Double.class, "double")
            .build()),
    FILE("file",  new ImmutableMap.Builder<Class<?>, String>().build()),
    BOOLEAN(
            "boolean", new ImmutableMap.Builder<Class<?>, String>().build()),
    STRING("string",
            new ImmutableMap.Builder<Class<?>, String>().build()),
    DATE_ONLY("date-only", new ImmutableMap.Builder<Class<?>, String>().build()),
    TIME_ONLY(
            "time-only", new ImmutableMap.Builder<Class<?>, String>().build()),
    DATETIME_ONLY("datetime-only", new ImmutableMap.Builder<Class<?>, String>().build()),
    DATETIME("datetime",new ImmutableMap.Builder<Class<?>, String>().build()),
    // All "integer" types are mapped to raml's "integer". Not sure if that is correct.
    INTEGER("integer", new ImmutableMap.Builder<Class<?>, String>()
            .put(int.class, "int32")
            .put(Integer.class, "int32")
            .put(short.class, "int16")
            .put(Short.class, "int16")
            .put(byte.class, "int8")
            .put(Byte.class, "int8")
            .put(long.class, "int64")
            .put(Long.class, "int64")
            .build()),
    NIL("nil", new ImmutableMap.Builder<Class<?>, String>().build());

    private static final Map<Type, ScalarType> JAVA_TO_RAML_TYPES;

    static {
        ImmutableMap.Builder<Type, ScalarType> builder = ImmutableMap.builder();
        builder.put(byte.class, INTEGER);
        builder.put(Byte.class, INTEGER);
        builder.put(short.class, INTEGER);
        builder.put(Short.class, INTEGER);
        builder.put(int.class, INTEGER);
        builder.put(Integer.class, INTEGER);
        builder.put(long.class, INTEGER);
        builder.put(Long.class, INTEGER);
        builder.put(InputStream.class, FILE);
        builder.put(Date.class, DATE_ONLY);

        builder.put(String.class, STRING);

        builder.put(float.class, NUMBER);
        builder.put(Float.class, NUMBER);
        builder.put(double.class, NUMBER);
        builder.put(Double.class, NUMBER);

        builder.put(boolean.class, BOOLEAN);
        builder.put(Boolean.class, BOOLEAN);

        JAVA_TO_RAML_TYPES = builder.build();
    }

    private static final Set<String> SCALAR_TYPES = new HashSet<>();
    static {
        for (ScalarType scalarType : ScalarType.values()) {

            SCALAR_TYPES.add(scalarType.ramlSyntax);
        }
    }

    private final String ramlSyntax;
    private final Map<Class<?>, String> formats;

    ScalarType(String ramlSyntax, Map<Class<?>, String> formats) {
        this.ramlSyntax = ramlSyntax;
        this.formats = formats;
    }

    @Override
    public TypeBuilder getRamlSyntax() {
        return TypeBuilder.type(ramlSyntax);
    }


    @Override
    public boolean isScalar() {
        return true;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public Class<?> type() {
        return null;
    }


    public static Optional<RamlType> fromType(final Type type) {

        final ScalarType foundType = JAVA_TO_RAML_TYPES.get(type);
        if (foundType == null) {
            return Optional.absent();
        }

        RamlType wrappedType = new RamlType() {
            @Override
            public TypeBuilder getRamlSyntax() {

                String format = foundType.formats.get(type);
                if ( format == null ) {
                    return foundType.getRamlSyntax();
                } else {
                    return foundType.getRamlSyntax().with(PropertyValueBuilder.property("format", format));
                }
            }

            @Override
            public boolean isScalar() {
                return true;
            }

            @Override
            public boolean isEnum() {
                return false;
            }

            @Override
            public Class<?> type() {
                return (Class<?>) type;
            }
        };

        return Optional.of(wrappedType);
    }

    public static boolean  isRamalScalarType(String type) {

        return SCALAR_TYPES.contains(type);
    }

}
