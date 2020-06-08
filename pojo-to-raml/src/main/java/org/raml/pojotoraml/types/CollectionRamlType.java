package org.raml.pojotoraml.types;

import org.raml.builder.TypeShapeBuilder;

/**
 * Created. There, you have it.
 */
public class CollectionRamlType  implements RamlType{

    private final RamlType type;

    CollectionRamlType(RamlType type) {

        this.type = type;
    }

    @Override
    public TypeShapeBuilder getRamlSyntax() {
        return TypeShapeBuilder.arrayOf(type.getRamlSyntax());
    }

    @Override
    public boolean isScalar() {
        return type.isScalar();
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public Class<?> type() {
        return type.type();
    }

    public static CollectionRamlType of(RamlType type) {

        return new CollectionRamlType(type);
    }
}
