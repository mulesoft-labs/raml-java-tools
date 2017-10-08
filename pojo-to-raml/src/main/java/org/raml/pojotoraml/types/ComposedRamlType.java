package org.raml.pojotoraml.types;

/**
 * Created. There, you have it.
 */
public class ComposedRamlType implements RamlType{

    private final Class<?> cls;
    private final String actualRamlName;

    public ComposedRamlType(Class<?> cls, String actualRamlName) {

        this.cls = cls;
        this.actualRamlName = actualRamlName;
    }

    public static ComposedRamlType forClass(Class<?> cls, String actualRamlName ) {

        return new ComposedRamlType(cls, actualRamlName);
    }

    @Override
    public String getRamlSyntax() {
        return actualRamlName;
    }

    @Override
    public boolean isScalar() {
        return false;
    }

    @Override
    public Class<?> type() {
        return cls;
    }
}
