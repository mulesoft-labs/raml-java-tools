package org.raml.ramltopojo;

import com.google.common.base.Optional;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created. There, you have it.
 */
public class CreationResult {

    private final String packageName;
    private final TypeSpec interf;
    private final TypeSpec impl;

    private final ArrayList<CreationResult> internalTypes = new ArrayList<>();

    public static CreationResult forType(String packageName, TypeSpec interf, TypeSpec impl) {

        return new CreationResult(packageName, interf, impl);
    }

    public static CreationResult forEnumeration(String packageName, TypeSpec enumeration) {

        return new CreationResult(packageName, enumeration, null);
    }

    private CreationResult(String packageName, TypeSpec interf, TypeSpec impl) {
        this.packageName = packageName;
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

    public void createType(String rootDirectory) throws IOException {

        JavaFile.builder(packageName, interf).skipJavaLangImports(true).build().writeTo(Paths.get(rootDirectory));
        if ( impl != null ) {

            JavaFile.builder(packageName, impl).skipJavaLangImports(true).build().writeTo(Paths.get(rootDirectory));
        }
    }
}
