package org.raml.pojotoraml;

import org.raml.pojotoraml.field.FieldClassParser;

/**
 * Created. There, you have it.
 */
public class PojoToRamlBuilder {

    public static PojoToRaml create() {
        return new PojoToRamlImpl(FieldClassParser.factory(), RamlAdjuster.NULL_ADJUSTER);
    }

    public static PojoToRaml create(ClassParserFactory factory, RamlAdjuster adjuster) {

        return new PojoToRamlImpl(factory, adjuster);
    }

    public static void main(String[] args) {
        PojoToRaml pojoToRaml = create();
        Result result = pojoToRaml.classToRaml(null);
    }
}
