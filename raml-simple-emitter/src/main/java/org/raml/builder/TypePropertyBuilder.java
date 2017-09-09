package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class TypePropertyBuilder extends KeyValueNodeBuilder<TypePropertyBuilder> implements AnnotableBuilder<TypePropertyBuilder> {

    private final String type;
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    public TypePropertyBuilder(String name, String type) {

        super(name);
        this.type = type;
    }

    public static TypePropertyBuilder property(String name, String type) {

        return new TypePropertyBuilder(name, type);
    }

    @Override
    public TypePropertyBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public KeyValueNode buildNode() {

        KeyValueNode node = super.buildNode();
        node.getValue().addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl(type)));
        if ( ! annotations.isEmpty() ) {

            for (AnnotationBuilder annotation : annotations) {
                node.getValue().addChild(annotation.buildNode());
            }
        }

        return node;
    }

}
