package org.raml.pojotoraml.types;

import org.raml.builder.DeclaredShapeBuilder;
import org.raml.builder.TypeShapeBuilder;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Created. There, you have it.
 */
public class CollectionRamlType  implements RamlType{

    private final RamlType type;

    CollectionRamlType(RamlType type) {

        this.type = type;
    }

    @Override
    public DeclaredShapeBuilder<?> getRamlSyntax(Function<Type, TypeShapeBuilder<?, ?>> builder) {
        return DeclaredShapeBuilder.anonymousType().ofType(TypeShapeBuilder.arrayOf(builder.apply(type.type())));
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
    public Type type() {
        return type.type();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    public static CollectionRamlType of(RamlType type) {

        return new CollectionRamlType(type);
    }
}
