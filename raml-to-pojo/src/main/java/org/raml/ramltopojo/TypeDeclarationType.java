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
import org.raml.ramltopojo.object.ObjectTypeHandlerPlugin;
import org.raml.ramltopojo.union.UnionTypeHandler;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public enum TypeDeclarationType implements TypeHandlerFactory, TypeAnalyserFactory {

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
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            return new ObjectTypeHandler((ObjectTypeDeclaration) typeDeclaration);
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            CreationResult result = generationContext.findCreatedType(typeName, originalTypeDeclaration);
            if ( eventType == EventType.INTERFACE ) {
                return ClassName.bestGuess(result.getInterface().name);
            } else {
                return ClassName.bestGuess(result.getImplementation().or(result.getInterface()).name);
            }
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
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return null;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return true;
        }
    },
    ARRAY {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {

            ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) originalTypeDeclaration;
            return ParameterizedTypeName.get(ClassName.get(List.class), TypeDeclarationType.javaType(typeName, arrayTypeDeclaration.items(), generationContext, eventType).box());
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    UNION {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            return new UnionTypeHandler((UnionTypeDeclaration) typeDeclaration);
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return null;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {

            return false;
        }
    },
    INTEGER {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return TypeName.INT;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    BOOLEAN {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return TypeName.BOOLEAN;
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATE {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATETIME {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    TIME_ONLY {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATETIME_ONLY {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return ClassName.get(Date.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    NUMBER {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return ClassName.get(BigDecimal.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    STRING {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            StringTypeDeclaration declaration = (StringTypeDeclaration) typeDeclaration;
            if ( ! declaration.enumValues().isEmpty() ) {
                return new EnumerationTypeHandler(declaration);
            } else {

                throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
            }
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return ClassName.get(String.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    ANY {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
            return ClassName.get(Object.class);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    FILE {
        @Override
        public TypeHandler createHandler(TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            throw new IllegalArgumentException("can't handle " + typeDeclaration.getClass());
        }

        @Override
        public TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType) {
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


    public abstract TypeName asJavaPoetType(String typeName, TypeDeclaration originalTypeDeclaration, GenerationContext generationContext, EventType eventType);
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

    /**
     * Create the actual type.
     *
     * @param typeDeclaration
     * @param context
     * @return
     */
    public static CreationResult createType(TypeDeclaration typeDeclaration, GenerationContext context) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        TypeHandler handler = typeDeclarationType.createHandler(typeDeclarationType, typeDeclaration);
        ClassName intf = handler.javaTypeName(context, EventType.INTERFACE);
        ClassName impl = handler.javaTypeName(context, EventType.IMPLEMENTATION);
        CreationResult creationResult = new CreationResult(context.defaultPackage(), intf, impl);
        context.newExpectedType(typeDeclaration.name(), creationResult);
        return handler.create(context, creationResult);
    }

    /**
     * Create the actual type.
     *
     * @param typeDeclaration
     * @param context
     * @return
     */
    public static CreationResult createInlineType(String name, TypeDeclaration typeDeclaration, final GenerationContext context) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        TypeHandler handler = typeDeclarationType.createHandler(typeDeclarationType, typeDeclaration);
        ClassName intf = handler.javaTypeName(new InlineGenerationContext(context),  EventType.INTERFACE);
        ClassName impl = handler.javaTypeName(new InlineGenerationContext(context), EventType.IMPLEMENTATION);
        CreationResult preCreationResult = new CreationResult("", intf, impl);
        return handler.create(context, preCreationResult);
    }

    /**
     * Find the typename to create.
     *
     * @param typeDeclaration
     * @param context
     * @param eventType
     * @return
     */
    public static ClassName typeName(TypeDeclaration typeDeclaration, GenerationContext context, EventType eventType) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        TypeHandler handler = typeDeclarationType.createHandler(typeDeclarationType, typeDeclaration);
        return handler.javaTypeName(context, eventType);
    }

    /**
     * Find the declared type name. This does not create the type:  it's for the hierarchy types and
     * dependent types where you have the RAML type name and need the Java type.
     *
     * @param typeName
     * @param typeDeclaration
     * @param generationContext
     * @param eventType
     * @return
     */
    public static TypeName javaType(String typeName, TypeDeclaration typeDeclaration, GenerationContext generationContext, EventType eventType) {

        return ramlToType.get(Utils.declarationType(typeDeclaration)).asJavaPoetType(typeName, typeDeclaration, generationContext, eventType);
    }

 /*   public static TypeAnalyser typeAnalyser(TypeDeclaration typeDeclaration) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        return typeDeclarationType.analyse(typeDeclarationType, typeDeclaration);
    }
*/


    public static boolean isNewInlineType(TypeDeclaration declaration) {
        return ramlToType.get(Utils.declarationType(declaration)).shouldCreateInlineType(declaration);
    }

    private static class InlineGenerationContext implements GenerationContext {
        private final GenerationContext context;

        public InlineGenerationContext(GenerationContext context) {
            this.context = context;
        }

        @Override
        public CreationResult findCreatedType(String typeName, TypeDeclaration ramlType) {
            return context.findCreatedType(typeName, ramlType);
        }

        @Override
        public String defaultPackage() {
            return "";
        }

        @Override
        public void newExpectedType(String name, CreationResult creationResult) {

        }

        @Override
        public void createTypes(String rootDirectory) throws IOException {

        }

        @Override
        public ObjectTypeHandlerPlugin pluginsForObjects(TypeDeclaration... typeDeclarations) {
            return context.pluginsForObjects(typeDeclarations);
        }

        @Override
        public Api api() {
            return context.api();
        }

        @Override
        public Set<String> childClasses(String ramlTypeName) {
            return context.childClasses(ramlTypeName);
        }
    }
}
