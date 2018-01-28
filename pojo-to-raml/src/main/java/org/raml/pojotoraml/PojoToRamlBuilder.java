package org.raml.pojotoraml;

import org.raml.pojotoraml.field.FieldClassParser;

/**
 * Created. There, you have it.
 */
public class PojoToRamlBuilder {

    public static PojoToRaml create() {
        return new PojoToRamlImpl(FieldClassParser.factory(), new AdjusterFactory() {
            @Override
            public RamlAdjuster createAdjuster(Class<?> clazz) {
                return RamlAdjuster.NULL_ADJUSTER;
            }
        });
    }

    public static PojoToRaml create(ClassParserFactory factory, AdjusterFactory adjusterFactory) {

        return new PojoToRamlImpl(factory, adjusterFactory);
    }

    public static void main(String[] args) {
        PojoToRaml pojoToRaml = create();
        Result result = pojoToRaml.classToRaml(null);
    }
}
