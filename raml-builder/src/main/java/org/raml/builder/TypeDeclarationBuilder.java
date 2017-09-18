package org.raml.builder;

import org.raml.yagi.framework.nodes.Node;

/**
 * Created. There, you have it.
 */
public class TypeDeclarationBuilder extends KeyValueNodeBuilder<TypeDeclarationBuilder> implements NodeBuilder {

    private final String name;
    private TypeBuilder types = null;

    private TypeDeclarationBuilder(String name) {
        super(name);
        this.name = name;
    }

    static public TypeDeclarationBuilder typeDeclaration(String name) {

        return new TypeDeclarationBuilder(name);
    }

    public TypeDeclarationBuilder ofType(TypeBuilder builder) {

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
