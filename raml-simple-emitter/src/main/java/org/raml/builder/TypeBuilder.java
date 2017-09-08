package org.raml.builder;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class TypeBuilder extends ObjectNodeBuilder<TypeBuilder> implements NodeBuilder {

    private List<NodeBuilder> properties = new ArrayList<>();
    public String name;

    private TypeBuilder(String name) {
        this.name = name;
    }

    static public TypeBuilder type(String name) {

        return new TypeBuilder(name);
    }

    @Override
    public ObjectNode buildNode() {

        TypeDeclarationNode node = new TypeDeclarationNode();
        node.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl(name)));

        if ( ! properties.isEmpty() ) {

            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("properties"), new ObjectNodeImpl());
            for (NodeBuilder property : properties) {
                kvn.getValue().addChild(property.buildNode());
            }

            node.addChild(kvn);
        }

        return node;
    }

    public TypeBuilder withProperty(NodeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }


}
