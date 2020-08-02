package org.raml.pojotoraml;

import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.types.RamlType;

import java.lang.reflect.Type;

/**
 * Created. There, you have it.
 */
class GeneratedRamlType implements RamlType {
    private final Type clazz;
    private final TypeShapeBuilder tb;

    public GeneratedRamlType(Type clazz, TypeShapeBuilder tb) {
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
    public Type type() {
        return clazz;
    }
}
