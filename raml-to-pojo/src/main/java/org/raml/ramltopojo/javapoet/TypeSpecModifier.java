package org.raml.ramltopojo.javapoet;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
@RequiredArgsConstructor(staticName = "modify")
public class TypeSpecModifier {

    private final TypeSpec.Builder typeSpec;

    private <T> Collection<T> given(Collection<T> collection, UnaryOperator<Collection<T>> cloneListBuilder) {
        Collection<T> newList = cloneListBuilder.apply(collection);
        collection.clear();
        return newList;
    }
    public TypeSpecModifier modifyMethods(UnaryOperator<MethodSpec> methodOperator) {
        Collection<MethodSpec> oldList = given(typeSpec.methodSpecs, ArrayList::new);
        typeSpec.methodSpecs.addAll(oldList.stream().map(methodOperator).filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

    public TypeSpecModifier modifySuperInterfaces(UnaryOperator<TypeName> methodOperator) {
        Collection<TypeName> oldList = given(typeSpec.superinterfaces, ArrayList::new);
        typeSpec.addSuperinterfaces(oldList.stream().map(methodOperator).filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

}
