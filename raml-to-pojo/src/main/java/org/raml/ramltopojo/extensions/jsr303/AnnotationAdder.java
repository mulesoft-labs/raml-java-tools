package org.raml.ramltopojo.extensions.jsr303;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

/**
 * Created. There, you have it.
 */
public  abstract class AnnotationAdder {

    public abstract void addAnnotation(AnnotationSpec spec);
    public abstract TypeName typeName();

    public void addAnnotation(Class<? extends Annotation> annotation) {

        addAnnotation(AnnotationSpec.builder(annotation).build());
    };
}
