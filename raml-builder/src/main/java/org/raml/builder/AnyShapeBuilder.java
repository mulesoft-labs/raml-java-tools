package org.raml.builder;



/**
 * Created. There, you have it.
 */
public class AnyShapeBuilder extends KeyValueNodeBuilder<AnyShapeBuilder> implements NodeBuilder {

    private TypeShapeBuilder types = null;

    private AnyShapeBuilder(String name) {
        super(name);
    }

    static public AnyShapeBuilder typeDeclaration(String name) {

        return new AnyShapeBuilder(name);
    }

    public AnyShapeBuilder ofType(TypeShapeBuilder builder) {

        types = builder;
        return this;
    }

    @Override
    protected Node createValueNode() {
        if ( types != null ) {

            return types.buildNode();
        } else {

            return super.createValueNode();
        }
    }
}
