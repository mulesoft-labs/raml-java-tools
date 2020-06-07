package org.raml.ramltopojo.javapoet;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Created. There, you have it.
 */
@RequiredArgsConstructor(staticName = "modify")
public class ParameterSpecModifier {

    private final ParameterSpec.Builder parameterSpec;

    private final List<Runnable> thingsToDo = new ArrayList<>();

    public ParameterSpecModifier modifyAnnotations(UnaryOperator<AnnotationSpec> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(parameterSpec.annotations, ArrayList::new, parameterSpec::addAnnotations, operator));
        return this;
    }

    public ParameterSpecModifier modifyModifiers(UnaryOperator<Modifier> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withEllipsisOfSpecs(parameterSpec.modifiers, ArrayList::new, parameterSpec::addModifiers, Modifier[]::new, operator));
        return this;
    }

    public void modifyAll() {

        thingsToDo.forEach(Runnable::run);
    }

}
