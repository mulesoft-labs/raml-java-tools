package org.raml.pojotoraml.types;

import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.RamlAdjuster;

/**
 * Created. There, you have it.
 */
public class CollectionRamlType  implements RamlType{

    private final RamlType type;

    CollectionRamlType(RamlType type) {

        this.type = type;
    }

    @Override
    public DeclaredShapeBuilder<?> getRamlSyntax(RamlAdjuster builder) {
        return DeclaredShapeBuilder.typeDeclaration("array").ofType(TypeShapeBuilder.arrayOf(type.getRamlSyntax(null).asTypeShapeBuilder()));
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
