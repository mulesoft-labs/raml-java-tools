package org.raml.ramltopojo;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.enumeration.EnumerationTypeHandler;
import org.raml.ramltopojo.object.ObjectTypeHandler;
import org.raml.ramltopojo.union.UnionTypeHandler;
import org.raml.v2.api.model.v10.datamodel.*;

import javax.annotation.Nullable;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public enum TypeDeclarationType implements TypeHandlerFactory {

    /*
     private static Map<Class, Class<?>> ramlToType = ImmutableMap.<Class, Class<?>>builder()
      .put(IntegerTypeDeclaration.class, int.class)
      .put(BooleanTypeDeclaration.class, boolean.class)
      .put(DateTimeOnlyTypeDeclaration.class, Date.class)
      .put(TimeOnlyTypeDeclaration.class, Date.class)
      .put(DateTimeTypeDeclaration.class, Date.class).put(DateTypeDeclaration.class, Date.class)
      .put(NumberTypeDeclaration.class, BigDecimal.class)
      .put(StringTypeDeclaration.class, String.class).put(FileTypeDeclaration.class, File.class)
      .put(AnyTypeDeclaration.class, Object.class)
      .build();

  private static Map<String, Class<?>> stringScalarToType = ImmutableMap
      .<String, Class<?>>builder().put("integer", int.class).put("boolean", boolean.class)
      .put("date-time", Date.class).put("date", Date.class).put("number", BigDecimal.class)
      .put("string", String.class).put("file", File.class).build();

  // cheating: I know I only have one table for floats and ints, but the parser
  // should prevent problems.
  private static Map<String, Class<?>> properType = ImmutableMap.<String, Class<?>>builder()
      .put("float", float.class).put("double", double.class).put("int8", byte.class)
      .put("int16", short.class).put("int32", int.class).put("int64", long.class)
      .put("int", int.class).build();

  private static Map<String, Class<?>> properTypeObject = ImmutableMap.<String, Class<?>>builder()
      .put("float", Float.class).put("double", Double.class).put("int8", Byte.class)
      .put("int16", Short.class).put("int32", Integer.class).put("int64", Long.class)
      .put("int", Integer.class).build();

     */
    OBJECT {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            return new ObjectTypeHandler((ObjectTypeDeclaration) typeDeclaration);
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            CreationResult result = generationContext.findCreatedType(typeName, originalTypeDeclaration);
            return ClassName.bestGuess(result.getInterface().name);
        }


        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {

            List<TypeDeclaration> extended = declaration.parentTypes();
            if ( extended.size() > 1) {

                return true;
            }

            Set<String> allExtendedProps =
                    FluentIterable.from(extended).filter(ObjectTypeDeclaration.class)
                            .transformAndConcat(new Function<ObjectTypeDeclaration, Set<String>>() {

                                @Nullable
                                @Override
                                public Set<String> apply(@Nullable ObjectTypeDeclaration input) {
                                    return pullNames(input);
                                }
                            }).toSet();

            Set<String> typePropertyNames = pullNames((ObjectTypeDeclaration) declaration);
            return !Sets.difference(typePropertyNames, allExtendedProps).isEmpty();
        }
    },
    ENUMERATION {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return null;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return true;
        }
    },
    ARRAY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {

            ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) originalTypeDeclaration;
            return ParameterizedTypeName.get(ClassName.get(List.class), TypeDeclarationType.javaType(typeName, arrayTypeDeclaration.items(), generationContext).box());
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    UNION {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            return new UnionTypeHandler((UnionTypeDeclaration) typeDeclaration);
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return null;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {

            return false;
        }
    },
    INTEGER {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return TypeName.INT;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    BOOLEAN {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return TypeName.BOOLEAN;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATE {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATETIME {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    TIME_ONLY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATETIME_ONLY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    NUMBER {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(BigDecimal.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    STRING {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            StringTypeDeclaration declaration = (StringTypeDeclaration) typeDeclaration;
            if ( ! declaration.enumValues().isEmpty() ) {
                return new EnumerationTypeHandler(declaration);
            } else {

                throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
            }
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(String.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    ANY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Object.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    FILE {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(File.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    };

    private static Set<String> pullNames(ObjectTypeDeclaration extending) {
        return FluentIterable.from(extending.properties()).transform(new Function<TypeDeclaration, String>() {

            @Nullable
            @Override
            public String apply(@Nullable TypeDeclaration input) {
                return input.name();
            }
        }).toSet();
    }


    public abstract TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext);
    public abstract boolean shouldCreateInlineType(TypeDeclaration declaration);

    private static Map<Class, TypeDeclarationType> ramlToType = ImmutableMap.<Class, TypeDeclarationType>builder()
            .put(ObjectTypeDeclaration.class, OBJECT)
            .put(ArrayTypeDeclaration.class, ARRAY)
            .put(UnionTypeDeclaration.class, UNION)
            .put(DateTimeOnlyTypeDeclaration.class, DATETIME_ONLY)
            .put(IntegerTypeDeclaration.class, INTEGER)
            .put(BooleanTypeDeclaration.class, BOOLEAN)
            .put(TimeOnlyTypeDeclaration.class, TIME_ONLY)
            .put(DateTimeTypeDeclaration.class, DATETIME)
            .put(DateTypeDeclaration.class, DATE)
            .put(NumberTypeDeclaration.class, NUMBER)
            .put(StringTypeDeclaration.class, STRING)
            .put(FileTypeDeclaration.class, FILE)
            .put(AnyTypeDeclaration.class, ANY)
            .build();

    public static TypeHandler typeHandler(TypeDeclaration typeDeclaration) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        return typeDeclarationType.create(typeDeclarationType, typeDeclaration);
    }

    public static TypeName javaType(String typeName, TypeDeclaration typeDeclaration, GenerationContext generationContext) {

        return ramlToType.get(Utils.declarationType(typeDeclaration)).asJavaPoetType(typeName, typeDeclaration, generationContext);
    }

    public static boolean isNewInlineType(TypeDeclaration declaration) {
        return ramlToType.get(Utils.declarationType(declaration)).shouldCreateInlineType(declaration);
    }
}
