package org.raml.builder;

import amf.client.model.domain.DomainElement;
import org.raml.yagi.framework.nodes.KeyValueNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class PropertyShapeBuilder extends KeyValueNodeBuilder<PropertyShapeBuilder> implements AnnotableBuilder<PropertyShapeBuilder> {

    private final TypeShapeBuilder type;
    private Boolean required;
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    public PropertyShapeBuilder(String name, TypeShapeBuilder type) {

        super(name);
        this.type = type;
        this.required = true;
    }

    public static PropertyShapeBuilder property(String name, String type) {

        return new PropertyShapeBuilder(name, TypeShapeBuilder.type(type));
    }

    public static PropertyShapeBuilder property(String name, TypeShapeBuilder type) {

        return new PropertyShapeBuilder(name, type);
    }

    public PropertyShapeBuilder required(boolean required) {

        this.required = required;
        return this;
    }

    @Override
    public PropertyShapeBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public DomainElement buildNode() {

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
