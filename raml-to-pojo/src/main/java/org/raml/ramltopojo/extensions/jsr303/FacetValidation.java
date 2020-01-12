package org.raml.ramltopojo.extensions.jsr303;

import amf.client.model.domain.*;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.EcmaPattern;
import org.raml.ramltopojo.ScalarTypes;
import org.raml.ramltopojo.Utils;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created. There, you have it.
 */
public class FacetValidation {

    public static void addFacetsForAll(AnnotationAdder typeSpec, PropertyShape typeDeclaration) {

        if (typeDeclaration.minCount().value() > 0) {

            typeSpec.addAnnotation(AnnotationSpec.builder(NotNull.class).build());
        }
    }

    public static void addAnnotations(AnyShape typeDeclaration, AnnotationAdder adder) {


        if (ScalarTypes.isString(typeDeclaration)) {

            addFacetsForString(adder, (ScalarShape) typeDeclaration);
            return;
        }

        if (ScalarTypes.isNumber(typeDeclaration) || ScalarTypes.isInteger(typeDeclaration)) {

            addFacetsForNumbers(adder, (ScalarShape)typeDeclaration);
            return;
        }

        if (typeDeclaration instanceof ArrayShape) {

            addFacetsForArray(adder, (ArrayShape) typeDeclaration);
        }

        if (isFieldFromBuiltType(typeDeclaration)) {

            addFacetsForBuilt(adder);
        }
    }

    public static void addFacetsForArray(AnnotationAdder fieldSpec, ArrayShape typeDeclaration) {

        if ( isFieldFromBuiltType(Utils.items(typeDeclaration)) ) {

            fieldSpec.addAnnotation(Valid.class);
        }

        AnnotationSpec.Builder minMax = null;
        if (typeDeclaration.minItems().nonNull()) {

            minMax =
                    AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minItems());
        }

        if (typeDeclaration.maxItems().nonNull()) {

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
        if (!typeDeclaration.minLength().isNull()) {

            minMax =
                    AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minLength().value());
        }

        if (!typeDeclaration.maxLength().isNull()) {

            if (minMax == null) {
                minMax =
                        AnnotationSpec.builder(Size.class).addMember("max", "$L", typeDeclaration.maxLength().value());
            } else {

                minMax.addMember("max", "$L", typeDeclaration.maxLength());
            }
        }

        if (minMax != null) {
            typeSpec.addAnnotation(minMax.build());
        }

        if ( !typeDeclaration.pattern().isNullOrEmpty() ) {

            typeSpec.addAnnotation(AnnotationSpec.builder(Pattern.class).addMember("regexp", "$S", EcmaPattern.fromString(typeDeclaration.pattern().value()).asJavaPattern()).build());
        }
    }


    public static void addFacetsForNumbers(AnnotationAdder typeSpec, ScalarShape typeDeclaration) {

        if (! typeDeclaration.minimum().isNull()) {
            if (isInteger(typeSpec.typeName())) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Min.class)
                        .addMember("value", "$L", (long)(typeDeclaration.minimum().value())).build());
            }
        }

        if (! typeDeclaration.maximum().isNull()) {
            if (isInteger(typeSpec.typeName())) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Max.class)
                        .addMember("value", "$L", (long)(typeDeclaration.maximum().value())).build());
            }
        }
    }

    public static void addFacetsForBuilt(AnnotationAdder fieldSpec) {

       fieldSpec.addAnnotation(Valid.class);
    }

    public static boolean isFieldFromBuiltType(Shape typeDeclaration) {

        return typeDeclaration instanceof NodeShape || typeDeclaration instanceof UnionShape || typeDeclaration instanceof SchemaShape;
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
