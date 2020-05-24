package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Created. There, you have it.
 */
public class CreationResult {

    private final AnyShape originalShape;
    private final String packageName;
    private ClassName interfaceName;
    private ClassName implementationName;
    private BiConsumer<String, CreationResult> interf;

    private TypeSpec generatedInterface;

    private TypeSpec impl;

    private final Map<String, CreationResult> internalTypes = new HashMap<>();

    public CreationResult(AnyShape originalShape, String packageName, ClassName interfaceName, ClassName implementationName) {
        this.originalShape = originalShape;
        this.packageName = packageName;
        this.interfaceName = interfaceName;
        this.implementationName = implementationName;
    }

    public CreationResult withInterface(TypeSpec spec) {

        this.generatedInterface = spec;
        this.interf = (root, cr) -> {

            try {
                if (generatedInterface.typeSpecs.size() == 0) {
                    createInlineType( this);
                }

                createJavaFile(packageName, generatedInterface, root, true);
            } catch (IOException e) {
                throw new GenerationException(e);
            }
        };
        return this;
    }

    public CreationResult withInterface(BiConsumer<String, CreationResult> specConsumer) {

        this.interf = specConsumer;
        return this;
    }

    public CreationResult withImplementation(TypeSpec spec) {

        this.impl = spec;
        return this;
    }

    public TypeSpec getInterface() {
        return generatedInterface;
    }
    public Optional<TypeSpec> getImplementation() {
        return Optional.ofNullable(impl);
    }

    public AnyShape getOriginalShape() {
        return originalShape;
    }

    public void createType(String rootDirectory) throws IOException {

        // This is a bit wrong.  We are covering ourselves because
        // we a generating some types twice.  TODO fix this.
        interf.accept(rootDirectory, this);

        if ( implementationName != null ) {

            createJavaFile(packageName, impl, rootDirectory, false);
        }
    }

    protected void createJavaFile(String packageName, TypeSpec typeSpec, String rootDirectory, boolean interf ) throws IOException {

        if ( interf ) {

            JavaFile.builder(interfaceName.packageName(), typeSpec).skipJavaLangImports(true).build().writeTo(Paths.get(rootDirectory));
        } else {

            if ( typeSpec != null ) {
                JavaFile.builder(implementationName.packageName(), typeSpec).skipJavaLangImports(true).build().writeTo(Paths.get(rootDirectory));
            }
        }
    }

    private static void createInlineType(CreationResult containingResult) {


        for (CreationResult internalType: containingResult.internalTypes.values()) {

            // here I must check to see if the container is an interface, if it is, I must add static.
            createInlineType(internalType);
            TypeSpec.Builder internalBuilder = internalType.getInterface().toBuilder();
            if ( internalType.getInterface().kind == TypeSpec.Kind.CLASS) {
                internalBuilder.addModifiers(Modifier.STATIC);
            }
            containingResult.generatedInterface = containingResult.getInterface().toBuilder().addType(internalBuilder.build()).build();
            if ( containingResult.getImplementation().isPresent()) {
                if (internalType.getImplementation().isPresent() ) {
                    containingResult.impl = containingResult.getImplementation().get().toBuilder().addType(
                            internalType.getImplementation().get().toBuilder().
                                    addModifiers(Modifier.STATIC).build()).build();
                }
            } else {

                if ( internalType.getImplementation().isPresent() ) {

                    containingResult.generatedInterface = containingResult.getInterface().toBuilder().addType(internalType.getImplementation().get().toBuilder().addModifiers(Modifier.STATIC).build()).build();
                }
            }
        }
    }

    public CreationResult getInternalTypeForProperty(String inside) {
        return internalTypes.get(inside);
    }

    public ClassName getJavaName(EventType eventType) {
        if ( eventType == EventType.IMPLEMENTATION) {
            return implementationName;
        } else {

            return interfaceName;
        }
    }
    public CreationResult withInternalType(String name, CreationResult internal) {

        internalTypes.put(name, internal);
        return this;
    }

    public CreationResult internalType(String name) {
        return internalTypes.get(name);
    }

}
