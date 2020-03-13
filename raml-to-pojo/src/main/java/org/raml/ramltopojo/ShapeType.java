package org.raml.ramltopojo;

import amf.client.model.domain.*;
import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.array.ArrayTypeHandler;
import org.raml.ramltopojo.enumeration.EnumerationTypeHandler;
import org.raml.ramltopojo.nulltype.NullTypeHandler;
import org.raml.ramltopojo.object.ObjectTypeHandler;
import org.raml.ramltopojo.references.ReferenceTypeHandler;
import org.raml.ramltopojo.union.UnionTypeHandler;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created. There, you have it.
 */
public enum ShapeType implements TypeHandlerFactory, TypeAnalyserFactory {

    NULL {
        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {

            return false;
        }

        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {
            return new NullTypeHandler(name, typeDeclaration);
        }
    },
    SCALAR {

        @Override
        public boolean shouldCreateInlineType(AnyShape originalDeclaration) {

            ScalarShape declaration = (ScalarShape) originalDeclaration;

            if ( ! declaration.values().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(originalDeclaration);
            } else {
                return false;
            }
        }

        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            ScalarShape declaration = (ScalarShape) typeDeclaration;
            if ( ! declaration.values().isEmpty() ) {

                return ENUMERATION.createHandler(name, type, typeDeclaration);
            } else {
                return Optional.ofNullable(
                        scalarToType(declaration.dataType().value()))
                        .orElseThrow(() -> new GenerationException("no scalar type '" + declaration.dataType().value() + "'"))
                        .createHandler(name, type, typeDeclaration);
            }
        }
    },
    OBJECT {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {
            return new ObjectTypeHandler(name, (NodeShape) typeDeclaration);
        }


        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {

            return ExtraInformation.isInline(declaration);
        }
    },
    ENUMERATION {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            return new EnumerationTypeHandler(name, (ScalarShape) typeDeclaration);
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
        //    return "string".equals(declaration.name().value()) || "number".equals(declaration.name().value()) || "integer".equals(declaration.name().value());
            return ExtraInformation.isInline(declaration);
        }
    },
    ARRAY {
        @Override
        public TypeHandler createHandler(String name, final ShapeType type, final AnyShape typeDeclaration) {

            final ArrayShape arrayTypeDeclaration = (ArrayShape) typeDeclaration;

            return new ArrayTypeHandler(name, arrayTypeDeclaration);
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            ArrayShape arrayTypeDeclaration = (ArrayShape) declaration;
            return ShapeType.isNewInlineType(Utils.items(arrayTypeDeclaration));
        }
    },
    UNION {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            return new UnionTypeHandler(name, (UnionShape) typeDeclaration);
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {

            // this seems wrong.
            return ExtraInformation.isInline(declaration);
        }
    },
    INTEGER {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            ScalarShape integerTypeDeclaration = (ScalarShape) typeDeclaration;
            if (  ! integerTypeDeclaration.values().isEmpty() ) {
                return ENUMERATION.createHandler(name, type, typeDeclaration);
            } else {

                TypeName typeName = Optional.ofNullable(properType.get(integerTypeDeclaration.format().value())).orElse(TypeName.INT);
                return new ReferenceTypeHandler(typeDeclaration, Integer.class, typeName);
            }
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape originalTypeDeclaration) {
            ScalarShape declaration = (ScalarShape) originalTypeDeclaration;

            if (! declaration.values().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(declaration);
            } else {
                return false;
            }
        }
    },
    BOOLEAN {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            return new ReferenceTypeHandler(typeDeclaration, Boolean.class, TypeName.BOOLEAN);

        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            if (! declaration.values().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(declaration);
            } else {
                return false;
            }
        }
    },
    DATE {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            return false;
        }
    },
    DATETIME {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            return false;
        }
    },
    TIME_ONLY {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            return false;
        }
    },
    DATETIME_ONLY {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, Date.class, ClassName.get(Date.class));
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            return false;
        }
    },
    NUMBER {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            ScalarShape integerTypeDeclaration = (ScalarShape) typeDeclaration;
            if ( ! integerTypeDeclaration.values().isEmpty() ) {
                return ENUMERATION.createHandler(name, type, typeDeclaration);
            } else {

                TypeName typeName = Optional.ofNullable(properType.get(integerTypeDeclaration.format().value())).orElse(ClassName.get(Number.class));
                return new ReferenceTypeHandler(typeDeclaration, Number.class, typeName);
            }
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape originalTypeDeclaration) {

            ScalarShape declaration = (ScalarShape) originalTypeDeclaration;

            if ( ! declaration.values().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(declaration);
            } else {
                return false;
            }

        }
    },
    STRING {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            ScalarShape declaration = (ScalarShape) typeDeclaration;
            if ( ! declaration.values().isEmpty() ) {
                return ENUMERATION.createHandler(name, type, typeDeclaration);
            } else {

                return new ReferenceTypeHandler(typeDeclaration, String.class, ClassName.get(String.class));
            }
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape originalTypeDeclaration) {

            ScalarShape declaration = (ScalarShape) originalTypeDeclaration;

            if ( ! declaration.values().isEmpty() ) {

                return ENUMERATION.shouldCreateInlineType(declaration);
            } else {
                return false;
            }
        }
    },
    ANY {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {

            return new ReferenceTypeHandler(typeDeclaration, Object.class, ClassName.get(Object.class));
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            return false;
        }
    },
    FILE {
        @Override
        public TypeHandler createHandler(String name, ShapeType type, AnyShape typeDeclaration) {
            return new ReferenceTypeHandler(typeDeclaration, File.class, ClassName.get(File.class));
        }

        @Override
        public boolean shouldCreateInlineType(AnyShape declaration) {
            return false;
        }
    };

    private static Stream<String> pullNames(NodeShape extending) {

        return extending.properties().stream().map(t -> t.name().value());
    }

    private static Map<String, TypeName> properType = ImmutableMap.<String, TypeName>builder()
            .put("float", TypeName.FLOAT).put("double", TypeName.DOUBLE).put("int8", TypeName.BYTE)
            .put("int16", TypeName.SHORT).put("int32", TypeName.INT).put("int64", TypeName.LONG)
            .put("int", TypeName.INT).build();


    public abstract boolean shouldCreateInlineType(AnyShape declaration);

    private static Map<Class, ShapeType> ramlToType = ImmutableMap.<Class, ShapeType>builder()
            .put(NodeShape.class, OBJECT)
            .put(ArrayShape.class, ARRAY)
            .put(UnionShape.class, UNION)
            .put(ScalarShape.class, SCALAR)
            .put(FileShape.class, FILE)
            .put(AnyShape.class, ANY)
            .put(NilShape.class, NULL)
            .build();

    public static ShapeType ramlToType(Class  scalarType) {

        return ramlToType.get(scalarType);
    }

    private static Map<String, ShapeType> scalarTypes = ImmutableMap.<String, ShapeType>builder()
            .put(ScalarTypes.DATETIME_ONLY_SCALAR, DATETIME_ONLY)
            .put(ScalarTypes.INTEGER_SCALAR, INTEGER)
            .put(ScalarTypes.BOOLEAN_SCALAR, BOOLEAN)
            .put(ScalarTypes.TIME_ONLY_SCALAR, TIME_ONLY)
            .put(ScalarTypes.DATETIME_SCALAR, DATETIME)
            .put(ScalarTypes.DATE_ONLY_SCALAR, DATE)
            .put(ScalarTypes.NUMBER_SCALAR, NUMBER)
            .put(ScalarTypes.STRING_SCALAR, STRING)
            .build();

    public static ShapeType scalarToType(String cls) {

        return scalarTypes.get(cls);
    }


    public static TypeName calculateTypeName(String name, AnyShape typeDeclaration, GenerationContext context, EventType eventType) {

        ShapeType shapeType = ramlToType(typeDeclaration.getClass());

        TypeHandler handler = shapeType.createHandler(name, shapeType, typeDeclaration);
        TypeName typeName = handler.javaClassReference(context, eventType);
        context.setupTypeHierarchy(name, typeDeclaration);
        return typeName;
    }

    public static boolean isNewInlineType(AnyShape declaration) {
        return ramlToType(Utils.declarationType(declaration)).shouldCreateInlineType(declaration);
    }

}
