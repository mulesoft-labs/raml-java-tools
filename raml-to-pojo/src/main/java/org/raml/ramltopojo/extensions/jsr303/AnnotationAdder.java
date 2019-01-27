package org.raml.ramltopojo.extensions.jsr303;

import com.squareup.javapoet.AnnotationSpec;

import java.lang.annotation.Annotation;

/**
 * Created. There, you have it.
 */
public  abstract class AnnotationAdder {

    public abstract void addAnnotation(AnnotationSpec spec);
    public void addAnnotation(Class<? extends Annotation> annotation) {

        addAnnotation(AnnotationSpec.builder(annotation).build());
    };
}
