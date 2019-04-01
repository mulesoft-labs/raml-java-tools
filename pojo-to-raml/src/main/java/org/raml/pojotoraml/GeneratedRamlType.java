package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.pojotoraml.types.RamlType;

/**
 * Created. There, you have it.
 */
class GeneratedRamlType implements RamlType {
    private final Class<?> clazz;
    private final TypeBuilder tb;

    public GeneratedRamlType(Class<?> clazz, TypeBuilder tb) {
        this.clazz = clazz;
        this.tb = tb;
    }

    @Override
    public TypeBuilder getRamlSyntax() {
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
