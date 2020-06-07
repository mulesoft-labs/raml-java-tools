package org.raml.ramltopojo.javapoet;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Created. There, you have it.
 */
@RequiredArgsConstructor(staticName = "modify")
public class AnnotationSpecModifier {

    private final AnnotationSpec.Builder annotationSpec;

    private final List<Runnable> thingsToDo = new ArrayList<>();

    public AnnotationSpecModifier modifyEnumConstant(UnaryOperator<Map.Entry<String,List<CodeBlock>>> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withProvidedSingularMapOfSpecs(JavaPoetUtilities.privateStringMapField(annotationSpec, "members"), LinkedHashMap::new, (k,v) -> v.forEach(v1 -> annotationSpec.addMember(k, v1)), operator));
        return this;
    }

    /*
        javadoc = typeSpec.javadoc;
    */

    public void modifyAll() {

        thingsToDo.forEach(Runnable::run);
    }

}
