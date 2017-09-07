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
public class BodyBuilder extends KeyValueNodeBuilder<TypeDeclaration, BodyBuilder> implements NodeBuilder {

    private List<TypeBuilder> types = new ArrayList<>();

    private BodyBuilder(String name) {
        super(name);
    }

    static public BodyBuilder body(String type) {

        return new BodyBuilder(type);
    }

    public BodyBuilder withTypes(TypeBuilder... builder) {

        types.addAll(Arrays.asList(builder));
        return this;
    }

    public KeyValueNode buildNode() {

        KeyValueNode node =  super.buildNode();

        if ( ! types.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("type"), responsesValueNode);

            for (TypeBuilder type : types) {

                responsesValueNode.addChild(type.buildNode());
            }

            node.getValue().addChild(kvn);
        }

        return node;
    }
}
