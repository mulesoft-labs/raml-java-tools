package org.raml.ramltopojo.javapoet;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Created. There, you have it.
 */
@RequiredArgsConstructor(staticName = "modify")
public class TypeSpecModifier {

    private final TypeSpec.Builder typeSpec;

    private final List<Runnable> thingsToDo = new ArrayList<>();

    public TypeSpecModifier modifyMethods(UnaryOperator<MethodSpec> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(typeSpec.methodSpecs, ArrayList::new, typeSpec.methodSpecs::addAll, operator));
        return this;
    }

    public TypeSpecModifier modifySuperinterfaces(UnaryOperator<TypeName> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(typeSpec.superinterfaces, ArrayList::new, typeSpec::addSuperinterfaces, operator));
        return this;
    }

    public TypeSpecModifier modifyAnnotations(UnaryOperator<AnnotationSpec> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(typeSpec.annotations, ArrayList::new, typeSpec::addAnnotations, operator));
        return this;
    }

    public TypeSpecModifier modifyTypeVariables(UnaryOperator<TypeVariableName> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(typeSpec.typeVariables, ArrayList::new, typeSpec::addTypeVariables, operator));
        return this;
    }

    public TypeSpecModifier modifyEnumConstant(UnaryOperator<Map.Entry<String,TypeSpec>> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withSingularMapOfSpecs(typeSpec.enumConstants, LinkedHashMap::new, typeSpec::addEnumConstant, operator));
        return this;
    }

    public TypeSpecModifier modifyFields(UnaryOperator<FieldSpec> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(typeSpec.fieldSpecs, ArrayList::new, typeSpec::addFields, operator));
        return this;
    }

    public TypeSpecModifier modifyTypes(UnaryOperator<TypeSpec> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withCollectionOfSpecs(typeSpec.typeSpecs, ArrayList::new, typeSpec::addTypes, operator));
        return this;
    }

    public TypeSpecModifier modifyModifiers(UnaryOperator<Modifier> operator) {
        thingsToDo.add(() -> JavaPoetUtilities.withEllipsisOfSpecs(typeSpec.modifiers, ArrayList::new, typeSpec::addModifiers, Modifier[]::new, operator));
        return this;
    }

    /*
        javadoc = typeSpec.javadoc;
    */


    public void modifyAll() {

        thingsToDo.forEach(Runnable::run);
    }

}
