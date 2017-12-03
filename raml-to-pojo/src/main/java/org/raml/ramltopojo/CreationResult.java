package org.raml.ramltopojo;

import com.google.common.base.Optional;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class CreationResult {

    private final String packageName;
    private TypeSpec interf;
    private TypeSpec impl;

    private final Map<String, CreationResult> internalTypes = new HashMap<>();

    public static CreationResult forType(String packageName, TypeSpec interf, TypeSpec impl) {

        return new CreationResult(packageName, interf, impl);
    }

    public static CreationResult forEnumeration(String packageName, TypeSpec enumeration) {

        return new CreationResult(packageName, enumeration, null);
    }

    CreationResult(String packageName, TypeSpec interf, TypeSpec impl) {
        this.packageName = packageName;
        this.interf = interf;
        this.impl = impl;
    }

    public static Builder builder() {

        return new Builder();
    }


    public TypeSpec getInterface() {
        return interf;
    }

    public Optional<TypeSpec> getImplementation() {
        return Optional.fromNullable(impl);
    }

    public void createType(String rootDirectory) throws IOException {

        createInlineType(this);
        createJavaFile(packageName, interf, rootDirectory, true);

        if ( impl != null ) {

            createJavaFile(packageName, impl, rootDirectory, false);
        }
    }

    protected void createJavaFile(String packageName, TypeSpec typeSpec, String rootDirectory, boolean interf ) throws IOException {

        TypeSpec.Builder builder = typeSpec.toBuilder();
        JavaFile.builder(packageName, builder.build()).skipJavaLangImports(true).build().writeTo(Paths.get(rootDirectory));
    }

    private static void createInlineType(CreationResult containingResult) {


        for (CreationResult internalType: containingResult.internalTypes.values()) {

            createInlineType(internalType);
            containingResult.interf = containingResult.getInterface().toBuilder().addType(internalType.getInterface()).build();
            if ( containingResult.getImplementation().isPresent()) {
                if (internalType.getImplementation().isPresent() ) {
                    containingResult.impl = containingResult.getImplementation().get().toBuilder().addType(
                            internalType.getImplementation().get().toBuilder().
                                    addModifiers(Modifier.STATIC).build()).build();
                }
            } else {

                if ( internalType.getImplementation().isPresent() ) {
                    containingResult.interf = containingResult.getInterface().toBuilder().addType(internalType.getImplementation().get()).build();
                }
            }
        }
    }

    public static class Builder {

        public TypeSpec interf;
        public TypeSpec impl;
        public Map<String, CreationResult> internalTypes = new HashMap<>();

        public Builder withInterface(TypeSpec spec) {
            interf = spec;
            return this;
        }

        public Builder withImplementation(TypeSpec spec) {
            impl = spec;
            return this;
        }

        public Builder withInternalType(String name, CreationResult internal) {

            internalTypes.put(name, internal);
            return this;
        }

        public CreationResult build(GenerationContext context) {

            CreationResult result =  CreationResult.forType(context.defaultPackage(), interf, impl);
            result.internalTypes.putAll(internalTypes);

            return result;
        }
    }
}
