package org.raml.builder;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class TypeBuilder extends KeyValueNodeBuilder<TypeDeclaration, AnnotationBuilder> implements NodeBuilder {

    private List<NodeBuilder> properties = new ArrayList<>();

    private TypeBuilder(String name) {
        super(name);
    }

    static public TypeBuilder type(String name) {

        return new TypeBuilder(name);
    }

    @Override
    public KeyValueNode buildNode() {

        KeyValueNode node = super.buildNode();

        ObjectNodeImpl valueNode = new ObjectNodeImpl();
        KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("properties"), valueNode);
        node.getValue().addChild(kvn);

        for (NodeBuilder property : properties) {

            valueNode.addChild(property.buildNode());
        }

        return node;
    }

    public TypeBuilder withProperty(NodeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }

    public TypeDeclaration build() {

        return super.build(TypeDeclaration.class, buildNode());
    }
}
