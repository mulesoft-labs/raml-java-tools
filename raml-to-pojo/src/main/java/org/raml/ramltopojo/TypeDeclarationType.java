package org.raml.ramltopojo;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.array.ArrayTypeHandler;
import org.raml.ramltopojo.enumeration.EnumerationTypeHandler;
import org.raml.ramltopojo.extensions.*;
import org.raml.ramltopojo.object.ObjectTypeHandler;
import org.raml.ramltopojo.references.ReferenceTypeHandler;
import org.raml.ramltopojo.union.UnionTypeHandler;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
*/


    OBJECT {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            return new ObjectTypeHandler(name, (ObjectTypeDeclaration) typeDeclaration);
        }


        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {

            List<TypeDeclaration> extended = declaration.parentTypes();

            if ( extended.size() > 1) {

                return true;
            }

            Set<String> allExtendedProps;

            if ( extended.size() == 1  && extended.get(0).name().equals("object")) {

                allExtendedProps = Collections.emptySet();
            } else {
                allExtendedProps =
                        FluentIterable.from(extended).filter(ObjectTypeDeclaration.class)
                                .transformAndConcat(new Function<ObjectTypeDeclaration, Set<String>>() {

                                    @Nullable
                                    @Override
                                    public Set<String> apply(@Nullable ObjectTypeDeclaration input) {
                                        return pullNames(input);
                                    }
                                }).toSet();
            }

            Set<String> typePropertyNames = pullNames((ObjectTypeDeclaration) declaration);
            return !Sets.difference(typePropertyNames, allExtendedProps).isEmpty();
        }
    },
    ENUMERATION {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            return new EnumerationTypeHandler(name, typeDeclaration);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return "string".equals(declaration.type()) || "number".equals(declaration.type()) || "integer".equals(declaration.type());
        }
    },
    ARRAY {
        @Override
        public TypeHandler createHandler(String name, final TypeDeclarationType type, final TypeDeclaration typeDeclaration) {

            final ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) typeDeclaration;

            return new ArrayTypeHandler(name, arrayTypeDeclaration);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {

            ArrayTypeDeclaration arrayTypeDeclaration = (ArrayTypeDeclaration) declaration;
            return false;
        }
    },
    UNION {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            return new UnionTypeHandler(name, (UnionTypeDeclaration) typeDeclaration);
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {

            // this seems wrong.
            return declaration.name().contains("|") || declaration.type().contains("|");
        }
    },
    INTEGER {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            NumberTypeDeclaration integerTypeDeclaration = (NumberTypeDeclaration) typeDeclaration;
            if ( ! integerTypeDeclaration.enumValues().isEmpty() ) {
                return ENUMERATION.createHandler(name, type, typeDeclaration);
            } else {

                TypeName typeName = Optional.fromNullable(properType.get(integerTypeDeclaration.format())).or(TypeName.INT);
                return new ReferenceTypeHandler(typeDeclaration, Integer.class, typeName);
            }
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration originalTypeDeclaration) {
            IntegerTypeDeclaration declaration = (IntegerTypeDeclaration) originalTypeDeclaration;

            if ( ! declaration.enumValues().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(originalTypeDeclaration);
            } else {
                return false;
            }
        }
    },
    BOOLEAN {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            return new ReferenceTypeHandler(typeDeclaration, Boolean.class, TypeName.BOOLEAN);

        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATE {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATETIME {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    TIME_ONLY {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    DATETIME_ONLY {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    NUMBER {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            NumberTypeDeclaration integerTypeDeclaration = (NumberTypeDeclaration) typeDeclaration;
            if ( ! integerTypeDeclaration.enumValues().isEmpty() ) {
                return ENUMERATION.createHandler(name, type, typeDeclaration);
            } else {

                TypeName typeName = Optional.fromNullable(properType.get(integerTypeDeclaration.format())).or(ClassName.get(Number.class));
                return new ReferenceTypeHandler(typeDeclaration, Number.class, typeName);
            }
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration originalTypeDeclaration) {

            NumberTypeDeclaration declaration = (NumberTypeDeclaration) originalTypeDeclaration;

            if ( ! declaration.enumValues().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(originalTypeDeclaration);
            } else {
                return false;
            }

        }
    },
    STRING {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            StringTypeDeclaration declaration = (StringTypeDeclaration) typeDeclaration;
            if ( ! declaration.enumValues().isEmpty() ) {
                return ENUMERATION.createHandler(name, type, typeDeclaration);
            } else {

                return new ReferenceTypeHandler(typeDeclaration, String.class, ClassName.get(String.class));
            }
        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration originalTypeDeclaration) {

            StringTypeDeclaration declaration = (StringTypeDeclaration) originalTypeDeclaration;

            if ( ! declaration.enumValues().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(originalTypeDeclaration);
            } else {
                return false;
            }
        }
    },
    ANY {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {

            return new ReferenceTypeHandler(typeDeclaration, Object.class, ClassName.get(Object.class));

        }

        @Override
        public boolean shouldCreateInlineType(TypeDeclaration declaration) {
            return false;
        }
    },
    FILE {
        @Override
        public TypeHandler createHandler(String name, TypeDeclarationType type, TypeDeclaration typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, File.class, ClassName.get(File.class));
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

    private static Map<String, TypeName> properType = ImmutableMap.<String, TypeName>builder()
            .put("float", TypeName.FLOAT).put("double", TypeName.DOUBLE).put("int8", TypeName.BYTE)
            .put("int16", TypeName.SHORT).put("int32", TypeName.INT).put("int64", TypeName.LONG)
            .put("int", TypeName.INT).build();


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
    public static Optional<CreationResult> createType(TypeDeclaration typeDeclaration, GenerationContext context) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        TypeHandler handler = typeDeclarationType.createHandler(typeDeclaration.name(), typeDeclarationType, typeDeclaration);
        ClassName intf = handler.javaClassName(context, EventType.INTERFACE);
        ClassName impl = handler.javaClassName(context, EventType.IMPLEMENTATION);
        CreationResult creationResult = new CreationResult(context.defaultPackage(), intf, impl);
        context.newExpectedType(typeDeclaration.name(), creationResult);
        context.setupTypeHierarchy(typeDeclaration);
        return handler.create(context, creationResult);
    }

    /**
     * Create the actual type.
     *
     * @param typeDeclaration
     * @param context
     * @return
     */
    public static Optional<CreationResult> createNamedType(String name, TypeDeclaration typeDeclaration, GenerationContext context) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        TypeHandler handler = typeDeclarationType.createHandler(name, typeDeclarationType, typeDeclaration);
        ClassName intf = handler.javaClassName(context, EventType.INTERFACE);
        ClassName impl = handler.javaClassName(context, EventType.IMPLEMENTATION);
        CreationResult creationResult = new CreationResult(context.defaultPackage(), intf, impl);
        context.newExpectedType(name, creationResult);
        context.setupTypeHierarchy(typeDeclaration);
        return handler.create(context, creationResult);
    }

    /**
     * Create the actual type.
     *
     * @param typeDeclaration
     * @param context
     * @return
     */
    public static Optional<CreationResult> createInlineType(ClassName containingClassName, ClassName containingImplementation, String name, TypeDeclaration typeDeclaration, final GenerationContext context) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        TypeHandler handler = typeDeclarationType.createHandler(name, typeDeclarationType, typeDeclaration);
        ClassName intf = handler.javaClassName(new InlineGenerationContext(containingClassName, containingClassName, context),  EventType.INTERFACE);
        ClassName impl = handler.javaClassName(new InlineGenerationContext(containingClassName, containingImplementation, context), EventType.IMPLEMENTATION);
        CreationResult preCreationResult = new CreationResult("", intf, impl);
        return handler.create(context, preCreationResult);
    }


    public static TypeName calculateTypeName(String name, TypeDeclaration typeDeclaration, GenerationContext context, EventType eventType) {

        TypeDeclarationType typeDeclarationType = ramlToType.get(Utils.declarationType(typeDeclaration));

        TypeHandler handler = typeDeclarationType.createHandler(name, typeDeclarationType, typeDeclaration);
        TypeName typeName = handler.javaClassReference(context, eventType);
        context.setupTypeHierarchy(typeDeclaration);
        return typeName;
    }

    public static boolean isNewInlineType(TypeDeclaration declaration) {
        return ramlToType.get(Utils.declarationType(declaration)).shouldCreateInlineType(declaration);
    }

    private static class InlineGenerationContext implements GenerationContext {
        private final ClassName containingDeclaration;
        private final ClassName containingImplementation;
        private final GenerationContext context;

        public InlineGenerationContext(ClassName containingDeclaration, ClassName containingImplementation, GenerationContext context) {
            this.containingDeclaration = containingDeclaration;
            this.containingImplementation = containingImplementation;
            this.context = context;
        }

        @Override
        public TypeName createSupportClass(TypeSpec.Builder newSupportType) {
            return context.createSupportClass(newSupportType);
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
        public EnumerationTypeHandlerPlugin pluginsForEnumerations(TypeDeclaration... typeDeclarations) {
            return context.pluginsForEnumerations(typeDeclarations);
        }

        @Override
        public UnionTypeHandlerPlugin pluginsForUnions(TypeDeclaration... typeDeclarations) {
            return context.pluginsForUnions(typeDeclarations);
        }

        @Override
        public ArrayTypeHandlerPlugin pluginsForArrays(TypeDeclaration... typeDeclarations) {
            return context.pluginsForArrays(typeDeclarations);
        }

        @Override
        public Api api() {
            return context.api();
        }

        @Override
        public Set<String> childClasses(String ramlTypeName) {
            return context.childClasses(ramlTypeName);
        }

        @Override
        public ClassName buildDefaultClassName(String name, EventType eventType) {
            if ( eventType == EventType.INTERFACE ) {
                return containingDeclaration.nestedClass(name);
            } else {
                return containingImplementation.nestedClass(name);
            }
        }

        @Override
        public ReferenceTypeHandlerPlugin pluginsForReferences(TypeDeclaration... typeDeclarations) {
            return context.pluginsForReferences(typeDeclarations);
        }

        @Override
        public void setupTypeHierarchy(TypeDeclaration typeDeclaration) {
            context.setupTypeHierarchy(typeDeclaration);
        }
    }
}
