package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class TypePropertyBuilder extends KeyValueNodeBuilder<TypePropertyBuilder> implements AnnotableBuilder<TypePropertyBuilder> {

    private final TypeBuilder type;
    private Boolean required;
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    public TypePropertyBuilder(String name, TypeBuilder type) {

        super(name);
        this.type = type;
        this.required = true;
    }

    public static TypePropertyBuilder property(String name, String type) {

        return new TypePropertyBuilder(name, TypeBuilder.type(type));
    }

    public static TypePropertyBuilder property(String name, TypeBuilder type) {

        return new TypePropertyBuilder(name, type);
    }

    public TypePropertyBuilder required(boolean required) {

        this.required = required;
        return this;
    }

    @Override
    public TypePropertyBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public KeyValueNode buildNode() {

        KeyValueNode node = super.buildNode();
        node.setValue(type.buildNode());
        if ( ! required ) {
            addProperty(node.getValue(), "required", required);
        }
        if ( ! annotations.isEmpty() ) {

            for (AnnotationBuilder annotation : annotations) {
                node.getValue().addChild(annotation.buildNode());
            }
        }

        return node;
    }

}
