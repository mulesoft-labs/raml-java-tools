package org.raml.pojotoraml.types;

import org.raml.builder.TypeShapeBuilder;

/**
 * Created. There, you have it.
 */
public class EnumRamlType implements RamlType{

    private final Class<?> cls;
    private final String actualRamlName;

    public EnumRamlType(Class<?> cls, String actualRamlName) {

        this.cls = cls;
        this.actualRamlName = actualRamlName;
    }

    public static EnumRamlType forClass(Class<?> cls, String actualRamlName ) {

        return new EnumRamlType(cls, actualRamlName);
    }

    @Override
    public TypeShapeBuilder getRamlSyntax() {
        return TypeShapeBuilder.type(actualRamlName);
    }

    @Override
    public boolean isScalar() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return true;
    }

    @Override
    public Class<?> type() {
        return cls;
    }
}
