package org.raml.ramltopojo;

import com.google.common.base.Optional;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;

/**
 * Created. There, you have it.
 */
public class CreationResult {

    private final TypeSpec interf;
    private final TypeSpec impl;

    private final ArrayList<CreationResult> internalTypes = new ArrayList<>();

    public static CreationResult forType(TypeSpec interf, TypeSpec impl) {

        return new CreationResult(interf, impl);
    }

    public static CreationResult forEnumeration(TypeSpec enumeration) {

        return new CreationResult(enumeration, null);
    }

    private CreationResult(TypeSpec interf, TypeSpec impl) {
        this.interf = interf;
        this.impl = impl;
    }

    public void internalType(CreationResult creationResult) {

        this.internalTypes.add(creationResult);
    }

    public TypeSpec getInterface() {
        return interf;
    }

    public Optional<TypeSpec> getImplementation() {
        return Optional.fromNullable(impl);
    }
}
