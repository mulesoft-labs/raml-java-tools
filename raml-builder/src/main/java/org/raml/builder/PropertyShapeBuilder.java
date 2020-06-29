package org.raml.builder;

import amf.client.model.domain.PropertyShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class PropertyShapeBuilder extends DomainElementBuilder<PropertyShape, PropertyShapeBuilder> implements AnnotableBuilder<PropertyShapeBuilder> {

    private final TypeShapeBuilder<?,?> type;
    private final String name;
    private Boolean required;
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    public PropertyShapeBuilder(String name, TypeShapeBuilder type) {

        super();
        this.name = name;
        this.type = type;
        this.required = true;
    }
//
//    public static PropertyShapeBuilder property(String name, String type) {
//
//        return new PropertyShapeBuilder(name, TypeShapeBuilder.simpleType(type));
//    }

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
    public PropertyShape buildNodeLocally() {

        PropertyShape node = new PropertyShape();
        node.withName(name);
        node.withPath(name);
        node.withRange(type.buildNodeLocally());
//        if ( ! annotations.isEmpty() ) {
//
//            for (AnnotationBuilder annotation : annotations) {
//                node.getValue().addChild(annotation.buildNode());
//            }
//        }

        return node;
    }

}
