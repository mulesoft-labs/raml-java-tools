package org.raml.pojotoraml;

import org.raml.builder.DeclaredShapeBuilder;
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
    public DeclaredShapeBuilder<?> getRamlSyntax(RamlAdjuster builder) {
        return DeclaredShapeBuilder.typeDeclaration("generated").ofType(tb);
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
