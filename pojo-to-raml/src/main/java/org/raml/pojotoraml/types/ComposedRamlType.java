package org.raml.pojotoraml.types;

import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.TypeShapeBuilder;

import java.lang.reflect.Type;
import java.util.function.Function;

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
    public DeclaredShapeBuilder<?> getRamlSyntax(Function<Type, TypeShapeBuilder<?, ?>> builder) {
        return DeclaredShapeBuilder.typeDeclaration(actualRamlName).ofType(TypeShapeBuilder.inheritingObjectFromShapes());
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
    public boolean isCollection() {
        return false;
    }

    @Override
    public Type type() {
        return cls;
    }
}
