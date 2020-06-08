package org.raml.pojotoraml;

import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.types.RamlType;

/**
 * Created. There, you have it.
 */
class GeneratedRamlType implements RamlType {
    private final Class<?> clazz;
    private final TypeShapeBuilder tb;

    public GeneratedRamlType(Class<?> clazz, TypeShapeBuilder tb) {
        this.clazz = clazz;
        this.tb = tb;
    }

    @Override
    public TypeShapeBuilder getRamlSyntax() {
        return tb;
    }

    @Override
    public boolean isScalar() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public Class<?> type() {
        return clazz;
    }
}
