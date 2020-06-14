package org.raml.pojotoraml.types;

import org.raml.builder.TypeShapeBuilder;

/**
 * Created. There, you have it.
 */
public class ComposedRamlType implements RamlType{

    private final Class<?> cls;
    private final String actualRamlName;

    ComposedRamlType(Class<?> cls, String actualRamlName) {

        this.cls = cls;
        this.actualRamlName = actualRamlName;
    }

    public static ComposedRamlType forClass(Class<?> cls, String actualRamlName ) {

        return new ComposedRamlType(cls, actualRamlName);
    }

    @Override
    public TypeShapeBuilder getRamlSyntax() {
        return TypeShapeBuilder.simpleType(actualRamlName);
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
        return cls;
    }
}
