package org.raml.builder;

import amf.client.model.domain.DomainElement;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class ParameterBuilder extends KeyValueNodeBuilder<ParameterBuilder> {

    private TypeShapeBuilder type;
    private String displayName;
    private String description;
    private Boolean required;

    private ParameterBuilder(String name) {
        super(name);
    }

    public static ParameterBuilder parameter(String name) {

        return new ParameterBuilder(name);
    }

    public ParameterBuilder ofType(String name) {

        this.type = TypeShapeBuilder.type(name);
        return this;
    }

    public ParameterBuilder ofType(TypeShapeBuilder name) {

        this.type = name;
        return this;
    }

    @Override
    protected Node createValueNode() {
        return new TypeDeclarationNode();
    }

    @Override
    public DomainElement buildNode() {

        KeyValueNode node = super.buildNode();

        if ( type != null ) {

            node.getValue().addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), type.buildNode()));
        }

        addProperty(node.getValue(), "displayName", displayName);
        addProperty(node.getValue(), "description", description);
        addProperty(node.getValue(), "required", required);

        return node;
    }

    public ParameterBuilder displayName(String displayName) {

        this.displayName = displayName;
        return this;
    }

    public ParameterBuilder description(String description) {

        this.description = description;
        return this;
    }

    public ParameterBuilder required(boolean required) {

        this.required = required;
        return this;
    }

    public boolean required() {
        return required;
    }
}
