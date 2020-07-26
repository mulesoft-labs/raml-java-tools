package org.raml.builder;


import amf.client.model.domain.AnyShape;
import com.google.common.base.Suppliers;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created. There, you have it.
 */
public class DeclaredShapeBuilder<T extends AnyShape> extends DomainElementBuilder<AnyShape, DeclaredShapeBuilder<T>> {

    private final String name;
    private TypeShapeBuilder<?, ?> type = null;

    private final Supplier<AnyShape> response;

    private DeclaredShapeBuilder(String name) {
        super();
        this.name = name;
        this.response = Suppliers.memoize(() -> calculateShape(name));
    }

    private AnyShape calculateShape(String name) {

        AnyShape shape = type.buildNode();
        Optional.ofNullable(name).ifPresent(n -> shape.withName(name));
        return shape;
    }

    static public<T extends AnyShape> DeclaredShapeBuilder<T> typeDeclaration(String name) {

        return new DeclaredShapeBuilder<>(name);
    }

    static public<T extends AnyShape> DeclaredShapeBuilder<T> anonymousType() {

        return new DeclaredShapeBuilder<>(null);
    }

    public DeclaredShapeBuilder<T> ofType(TypeShapeBuilder<?,?> builder) {

        type = builder;
        return this;
    }

    public TypeShapeBuilder<?,?> asTypeShapeBuilder() {

        Optional.ofNullable(name).ifPresent(n -> type.withName(n));

        return type;
    }

    @Override
    protected AnyShape buildNodeLocally() {

        return response.get();
    }

    public String name() {
        return name;
    }

    public boolean isAnonymous() {
        return name == null;
    }
}
