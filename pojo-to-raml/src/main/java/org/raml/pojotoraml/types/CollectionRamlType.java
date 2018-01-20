package org.raml.pojotoraml.types;

/**
 * Created. There, you have it.
 */
public class CollectionRamlType  implements RamlType{

    private final RamlType type;

    CollectionRamlType(RamlType type) {

        this.type = type;
    }

    @Override
    public String getRamlSyntax() {
        return type.getRamlSyntax() + "[]";
    }

    @Override
    public boolean isScalar() {
        return type.isScalar();
    }

    @Override
    public Class<?> type() {
        return type.type();
    }

    public static CollectionRamlType of(RamlType type) {

        return new CollectionRamlType(type);
    }
}
