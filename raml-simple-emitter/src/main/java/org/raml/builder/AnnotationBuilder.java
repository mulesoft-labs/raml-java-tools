package org.raml.builder;

/**
 * Created. There, you have it.
 */
public class AnnotationBuilder extends KeyValueNodeBuilder<AnnotationBuilder> implements NodeBuilder {

    private AnnotationBuilder(String name) {
        super(name);
    }

    static public AnnotationBuilder annotation(String name) {

        return new AnnotationBuilder(name);
    }
}
