package org.raml.builder;


import amf.client.model.domain.AnyShape;
import com.google.common.base.Suppliers;

import java.util.function.Supplier;

/**
 * Created. There, you have it.
 */
public class DeclaredShapeBuilder extends KeyValueNodeBuilder<DeclaredShapeBuilder> implements NodeBuilder {

    private final String name;
    private TypeShapeBuilder<?, ?> types = null;

    private Supplier<AnyShape> response;

    private DeclaredShapeBuilder(String name) {
        super(name);
        this.name = name;
        this.response = Suppliers.memoize(() -> calculateShape(name));
    }

    private AnyShape calculateShape(String name) {

        AnyShape shape = types.buildNode();
        shape.withName(name);
        return shape;
    }

    static public DeclaredShapeBuilder typeDeclaration(String name) {

        return new DeclaredShapeBuilder(name);
    }

    public DeclaredShapeBuilder ofType(TypeShapeBuilder<?,?> builder) {

        types = builder;
        return this;
    }

    @Override
    public AnyShape buildNode() {

        return response.get();
    }
}
