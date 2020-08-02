package org.raml.pojotoraml;

import org.raml.pojotoraml.field.FieldClassParser;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
public class PojoToRamlBuilder {

    public static PojoToRaml create() {
        return new PojoToRamlImpl(FieldClassParser.factory(), new AdjusterFactory() {
            @Override
            public RamlAdjuster createAdjuster(Type clazz) {
                return RamlAdjuster.NULL_ADJUSTER;
            }
        });
    }

    public static PojoToRaml create(ClassParserFactory factory, AdjusterFactory adjusterFactory) {

        return new PojoToRamlImpl(factory, adjusterFactory);
    }
}
