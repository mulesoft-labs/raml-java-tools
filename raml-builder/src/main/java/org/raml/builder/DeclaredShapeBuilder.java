package org.raml.builder;


import amf.client.model.domain.AnyShape;
import com.google.common.base.Suppliers;

import java.util.function.Supplier;

/**
 * Created. There, you have it.
 */
public class DeclaredShapeBuilder<T extends AnyShape> extends DomainElementBuilder<T, DeclaredShapeBuilder<T>> {

    private final String name;
    private TypeShapeBuilder<T, ?> types = null;

    private final Supplier<T> response;

    private DeclaredShapeBuilder(String name) {
        super();
        this.name = name;
        this.response = Suppliers.memoize(() -> calculateShape(name));
    }

    private T calculateShape(String name) {

        T shape = types.buildNode();
        shape.withName(name);
        return shape;
    }

    static public<T extends AnyShape> DeclaredShapeBuilder<T> typeDeclaration(String name) {

        return new DeclaredShapeBuilder<>(name);
    }

    public DeclaredShapeBuilder<T> ofType(TypeShapeBuilder<T,?> builder) {

        types = builder;
        return this;
    }

    @Override
    public T buildNode() {

        return response.get();
    }
}
