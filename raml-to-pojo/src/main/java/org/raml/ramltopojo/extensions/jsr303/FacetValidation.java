package org.raml.ramltopojo.extensions.jsr303;

import amf.client.model.domain.*;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.EcmaPattern;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created. There, you have it.
 */
public class FacetValidation {

    public static void addFacetsForAll(AnnotationAdder typeSpec, PropertyShape typeDeclaration) {

        if (true /*typeDeclaration.required() != null && typeDeclaration.required()*/) {

            typeSpec.addAnnotation(AnnotationSpec.builder(NotNull.class).build());
        }
    }

    public static void addAnnotations(PropertyShape typeDeclaration, AnnotationAdder adder) {

        addFacetsForAll(adder, typeDeclaration);

        Shape range = typeDeclaration.range();
        if ((range instanceof ScalarShape) && "string".equals(((ScalarShape)range).dataType().value())) {

            addFacetsForString(adder, (ScalarShape) range);
            return;
        }

        if (range instanceof ScalarShape) {

            addFacetsForNumbers(adder, (ScalarShape)range);
            return;
        }

        if (range instanceof ArrayShape) {

            addFacetsForArray(adder, (ArrayShape) range);
        }

        if (isFieldFromBuiltType(range)) {

            addFacetsForBuilt(adder);
        }
    }

    public static void addFacetsForArray(AnnotationAdder fieldSpec, ArrayShape typeDeclaration) {

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

    public static void addFacetsForString(AnnotationAdder typeSpec, ScalarShape typeDeclaration) {

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

            typeSpec.addAnnotation(AnnotationSpec.builder(Pattern.class).addMember("regexp", "$S", EcmaPattern.fromString(typeDeclaration.pattern().value()).asJavaPattern()).build());
        }
    }


    public static void addFacetsForNumbers(AnnotationAdder typeSpec, ScalarShape typeDeclaration) {

        if (typeDeclaration.minimum() != null) {
            if (isInteger(typeSpec.typeName())) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Min.class)
                        .addMember("value", "$L", typeDeclaration.minimum().value()).build());
            }
        }

        if (typeDeclaration.maximum() != null) {
            if (isInteger(typeSpec.typeName())) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Max.class)
                        .addMember("value", "$L", typeDeclaration.maximum().value()).build());
            }
        }
    }

    public static void addFacetsForBuilt(AnnotationAdder fieldSpec) {

       fieldSpec.addAnnotation(Valid.class);
    }

    public static boolean isFieldFromBuiltType(Shape typeDeclaration) {

        return typeDeclaration instanceof UnionShape || typeDeclaration instanceof NodeShape;
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
