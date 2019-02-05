package org.raml.ramltopojo.extensions.jsr303;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.EcmaPattern;
import org.raml.v2.api.model.v10.datamodel.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created. There, you have it.
 */
public class FacetValidation {

    public static void addFacetsForAll(AnnotationAdder typeSpec, TypeDeclaration typeDeclaration) {

        if (typeDeclaration.required() != null && typeDeclaration.required()) {

            typeSpec.addAnnotation(AnnotationSpec.builder(NotNull.class).build());
        }
    }

    public static void addAnnotations(TypeDeclaration typeDeclaration, AnnotationAdder adder) {

        addFacetsForAll(adder, typeDeclaration);

        if (typeDeclaration instanceof NumberTypeDeclaration) {

            addFacetsForNumbers(adder, (NumberTypeDeclaration) typeDeclaration);
            return;
        }

        if (typeDeclaration instanceof StringTypeDeclaration) {

            addFacetsForString(adder, (StringTypeDeclaration) typeDeclaration);
        }

        if (typeDeclaration instanceof ArrayTypeDeclaration) {

            addFacetsForArray(adder, (ArrayTypeDeclaration) typeDeclaration);
        }

        if (isFieldFromBuiltType(typeDeclaration)) {

            addFacetsForBuilt(adder);
        }
    }

    public static void addFacetsForArray(AnnotationAdder fieldSpec, ArrayTypeDeclaration typeDeclaration) {

        if ( isFieldFromBuiltType(typeDeclaration.items()) ) {

            fieldSpec.addAnnotation(Valid.class);
        }

        AnnotationSpec.Builder minMax = null;
        if (typeDeclaration.minItems() != null) {

            minMax =
                    AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minItems());
        }

        if (typeDeclaration.maxItems() != null) {

            if (minMax == null) {
                minMax =
                        AnnotationSpec.builder(Size.class).addMember("max", "$L", typeDeclaration.maxItems());
            } else {

                minMax.addMember("max", "$L", typeDeclaration.maxItems());
            }
        }

        if (minMax != null) {
            fieldSpec.addAnnotation(minMax.build());
        }
    }

    public static void addFacetsForString(AnnotationAdder typeSpec, StringTypeDeclaration typeDeclaration) {

        AnnotationSpec.Builder minMax = null;
        if (typeDeclaration.minLength() != null) {

            minMax =
                    AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minLength());
        }

        if (typeDeclaration.maxLength() != null) {

            if (minMax == null) {
                minMax =
                        AnnotationSpec.builder(Size.class).addMember("max", "$L", typeDeclaration.maxLength());
            } else {

                minMax.addMember("max", "$L", typeDeclaration.maxLength());
            }
        }

        if (minMax != null) {
            typeSpec.addAnnotation(minMax.build());
        }

        if ( typeDeclaration.pattern() != null ) {

            typeSpec.addAnnotation(AnnotationSpec.builder(Pattern.class).addMember("regexp", "$S", EcmaPattern.fromString(typeDeclaration.pattern()).asJavaPattern()).build());
        }
    }


    public static void addFacetsForNumbers(AnnotationAdder typeSpec, NumberTypeDeclaration typeDeclaration) {

        if (typeDeclaration.minimum() != null) {
            if (isInteger(typeSpec.typeName())) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Min.class)
                        .addMember("value", "$L", typeDeclaration.minimum().intValue()).build());
            }
        }

        if (typeDeclaration.maximum() != null) {
            if (isInteger(typeSpec.typeName())) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Max.class)
                        .addMember("value", "$L", typeDeclaration.maximum().intValue()).build());
            }
        }
    }

    public static void addFacetsForBuilt(AnnotationAdder fieldSpec) {

       fieldSpec.addAnnotation(Valid.class);
    }

    public static boolean isFieldFromBuiltType(TypeDeclaration typeDeclaration) {

        return typeDeclaration instanceof ObjectTypeDeclaration || typeDeclaration instanceof UnionTypeDeclaration;
    }

    private static boolean isInteger(TypeName type) {

        return type.box().toString().equals(Integer.class.getName())
                || type.box().toString().equals(Short.class.getName())
                || type.box().toString().equals(Byte.class.getName())
                || type.box().toString().equals(BigDecimal.class.getName())
                || type.box().toString().equals(Long.class.getName())
                || type.box().toString().equals(BigInteger.class.getName());
    }

}
