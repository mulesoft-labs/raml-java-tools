package org.raml.builder;

/**
 * Created. There, you have it.
 */
public interface AnnotableBuilder<R extends AnnotableBuilder> {
    R withAnnotations(AnnotationBuilder... builders);
}
