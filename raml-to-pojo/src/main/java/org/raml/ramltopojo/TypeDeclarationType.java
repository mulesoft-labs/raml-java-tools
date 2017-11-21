package org.raml.ramltopojo;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.enumeration.EnumerationTypeHandler;
import org.raml.ramltopojo.object.ObjectTypeHandler;
import org.raml.v2.api.model.v10.datamodel.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public enum TypeDeclarationType implements TypeHandlerFactory {

    /*
     private static Map<Class, Class<?>> scalarToType = ImmutableMap.<Class, Class<?>>builder()
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
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            CreationResult result = generationContext.findCreatedType(originalTypeDeclaration);
            return ClassName.bestGuess(result.getInterface().name);
        }
    },
    ENUMERATION {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return null;
        }
    },
    ARRAY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {

            ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) originalTypeDeclaration;
            return ParameterizedTypeName.get(ClassName.get(List.class), TypeDeclarationType.javaType(arrayTypeDeclaration.items(), generationContext).box());
        }
    },
    UNION {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return null;
        }
    },
    INTEGER {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return TypeName.INT;
        }
    },
    BOOLEAN {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return TypeName.BOOLEAN;
        }

    },
    DATE {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }
    },
    DATETIME {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }
    },
    TIME_ONLY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }
    },
    DATETIME_ONLY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Date.class);
        }
    },
    NUMBER {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(BigDecimal.class);
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
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(String.class);
        }
    },
    ANY {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(Object.class);
        }
    },
    FILE {
        @Override
        public TypeHandler create(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext) {
            return ClassName.get(File.class);
        }
    };

    public abstract TypeName asJavaPoetType(TypeDeclaration originalTypeDeclaration, GenerationContext generationContext);

    private static Map<Class, TypeDeclarationType> scalarToType = ImmutableMap.<Class, TypeDeclarationType>builder()
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

        TypeDeclarationType typeDeclarationType = scalarToType.get(typeDeclaration.getClass().getInterfaces()[0]);

        return typeDeclarationType.create(typeDeclarationType, typeDeclaration);
    }

    public static TypeName javaType(TypeDeclaration typeDeclaration, GenerationContext generationContext) {

        return scalarToType.get(typeDeclaration.getClass().getInterfaces()[0]).asJavaPoetType(typeDeclaration, generationContext);
    }

}
