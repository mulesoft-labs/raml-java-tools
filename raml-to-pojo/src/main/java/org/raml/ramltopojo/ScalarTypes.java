package org.raml.ramltopojo;

import amf.client.model.domain.ScalarNode;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.Shape;

/**
 * Created. There, you have it. //
 */
public class ScalarTypes {
    public static final String DATETIME_ONLY_SCALAR = "http://a.ml/vocabularies/shapes#dateTimeOnly";
    public static final String INTEGER_SCALAR = "http://www.w3.org/2001/XMLSchema#integer";
    public static final String LONG_INTEGER_SCALAR = "http://www.w3.org/2001/XMLSchema#long";
    public static final String BOOLEAN_SCALAR = "http://www.w3.org/2001/XMLSchema#boolean";
    public static final String TIME_ONLY_SCALAR = "http://www.w3.org/2001/XMLSchema#time";
    public static final String DATETIME_SCALAR = "http://www.w3.org/2001/XMLSchema#dateTime";
    public static final String DATE_ONLY_SCALAR = "http://www.w3.org/2001/XMLSchema#date";
    public static final String NUMBER_SCALAR = "http://a.ml/vocabularies/shapes#number";
    public static final String STRING_SCALAR = "http://www.w3.org/2001/XMLSchema#string";

    public static boolean isDatetimeOnly(Shape shape) {

        return shape instanceof ScalarShape && ((ScalarShape)shape).dataType().is(DATETIME_ONLY_SCALAR);
    }

    public static boolean isInteger(Shape shape) {

        return shape instanceof ScalarShape && (((ScalarShape)shape).dataType().is(INTEGER_SCALAR) || ((ScalarShape)shape).dataType().is(INTEGER_SCALAR));
    }
    public static boolean isBoolean(Shape shape) {

        return shape instanceof ScalarShape && ((ScalarShape)shape).dataType().is(BOOLEAN_SCALAR);
    }
    public static boolean isTimeOnly(Shape shape) {

        return shape instanceof ScalarShape && ((ScalarShape)shape).dataType().is(TIME_ONLY_SCALAR);
    }
    public static boolean isDateTime(Shape shape) {

        return shape instanceof ScalarShape && ((ScalarShape)shape).dataType().is(DATETIME_SCALAR);
    }
    public static boolean isDateOnly(Shape shape) {

        return shape instanceof ScalarShape && ((ScalarShape)shape).dataType().is(DATE_ONLY_SCALAR);
    }
    public static boolean isNumber(Shape shape) {

        return shape instanceof ScalarShape && ((ScalarShape)shape).dataType().is(NUMBER_SCALAR);
    }
    public static boolean isString(Shape shape) {

        return shape instanceof ScalarShape && ((ScalarShape)shape).dataType().is(STRING_SCALAR);
    }

    public static ScalarNode SCALAR_NODE_TRUE = new ScalarNode("true", BOOLEAN_SCALAR);
    public static ScalarNode SCALAR_NODE_FALSE = new ScalarNode("false", BOOLEAN_SCALAR);

    public static ScalarNode stringNode(String value) {
        return new ScalarNode(value, STRING_SCALAR);
    }
}
