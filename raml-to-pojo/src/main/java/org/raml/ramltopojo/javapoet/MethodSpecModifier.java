package org.raml.ramltopojo.javapoet;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.raml.ramltopojo.javapoet.JavaPoetUtilities.privateCollectionField;

/**
 * Created. There, you have it.
 */
@RequiredArgsConstructor(staticName = "modify")
public class MethodSpecModifier {

    /*
    private final CodeBlock.Builder javadoc = CodeBlock.builder();
    private final CodeBlock.Builder code = CodeBlock.builder();
     */

    private final MethodSpec.Builder methodSpec;

    private final List<Runnable> thingsToDo = new ArrayList<>();


    public MethodSpecModifier modifyAnnotations(UnaryOperator<AnnotationSpec> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(methodSpec.annotations, ArrayList::new, methodSpec::addAnnotations, operator));
        return this;
    }

    public MethodSpecModifier modifyTypeVariables(UnaryOperator<TypeVariableName> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(methodSpec.typeVariables, ArrayList::new, methodSpec::addTypeVariables, operator));
        return this;
    }

    public MethodSpecModifier modifyModifiers(UnaryOperator<Modifier> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withEllipsisOfSpecs(methodSpec.modifiers, ArrayList::new, methodSpec::addModifiers, Modifier[]::new, operator));
        return this;
    }

    public MethodSpecModifier modifyParameters(UnaryOperator<ParameterSpec> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(methodSpec.parameters, ArrayList::new, methodSpec::addParameters, operator));
        return this;
    }

    public MethodSpecModifier modifyExceptions(UnaryOperator<TypeName> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withSuppliedCollectionOfSpecs(privateCollectionField(methodSpec, "exceptions"), LinkedHashSet::new, methodSpec::addExceptions, operator));
        return this;
    }

    public void modifyAll() {

        thingsToDo.forEach(Runnable::run);
    }

}
