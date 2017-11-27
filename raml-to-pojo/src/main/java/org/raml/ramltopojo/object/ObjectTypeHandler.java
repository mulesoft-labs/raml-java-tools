package org.raml.ramltopojo.object;

import com.google.common.base.Optional;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.*;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.lang.model.element.Modifier;

/**
 * Created. There, you have it.
 */
public class ObjectTypeHandler implements TypeHandler {

    private final ObjectTypeDeclaration objectTypeDeclaration;

    public ObjectTypeHandler(ObjectTypeDeclaration objectTypeDeclaration) {
        this.objectTypeDeclaration = objectTypeDeclaration;
    }

    @Override
    public CreationResult create(GenerationContext generationContext) {

        // I need to create an interface and an implementation.

        TypeSpec interfaceSpec = createInterface(generationContext);
        TypeSpec implementationSpec = createImplementation(interfaceSpec, generationContext);

        return CreationResult.forType(generationContext.defaultPackage(), interfaceSpec, implementationSpec);
    }

    private TypeSpec createImplementation(TypeSpec interfaceSpec, GenerationContext generationContext) {

        ClassName className = ClassName.get(generationContext.defaultPackage(), Names.typeName(objectTypeDeclaration.name(), "Impl"));
        TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .addSuperinterface(ClassName.bestGuess(interfaceSpec.name))
                .addModifiers(Modifier.PUBLIC);

        Optional<String> discriminator = Optional.fromNullable(objectTypeDeclaration.discriminator());

        for (TypeDeclaration declaration : objectTypeDeclaration.properties()) {

            TypeName tn = findType(declaration.type(), declaration, generationContext);

            FieldSpec.Builder field = FieldSpec.builder(tn, Names.variableName(declaration.name())).addModifiers(Modifier.PRIVATE);
            if ( declaration.name().equals(discriminator.orNull())) {

                field.addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer(CodeBlock.builder().add("$S", objectTypeDeclaration.name()).build());

            }

            typeSpec.addField(field.build());

            typeSpec.addMethod(MethodSpec.methodBuilder(Names.methodName("get", declaration.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(CodeBlock.builder().addStatement("return this." + Names.variableName(declaration.name())).build())
                    .returns(tn).build());

            if ( declaration.name().equals(discriminator.orNull())) {

                continue;
            }

            typeSpec.addMethod(MethodSpec.methodBuilder(Names.methodName("set", declaration.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(CodeBlock.builder().addStatement("this." + Names.variableName(declaration.name()) + " = " + Names.variableName(declaration.name())).build())
                    .addParameter(tn, Names.variableName(declaration.name())).build());
        }

        return typeSpec.build();
    }

    private TypeSpec createInterface(GenerationContext generationContext) {

        ClassName interf = ClassName.get(generationContext.defaultPackage(), Names.typeName(objectTypeDeclaration.name()));
        TypeSpec.Builder typeSpec = TypeSpec
                .interfaceBuilder(interf)
                .addModifiers(Modifier.PUBLIC);

        Optional<String> discriminator = Optional.fromNullable(objectTypeDeclaration.discriminator());

        for (TypeDeclaration typeDeclaration : objectTypeDeclaration.parentTypes()) {

            if (typeDeclaration instanceof ObjectTypeDeclaration) {

                if (typeDeclaration.name().equals("object")) {
                    continue;
                }

                TypeName inherits = findType(typeDeclaration.name(), typeDeclaration, generationContext);
                typeSpec.addSuperinterface(inherits);
            } else {

                throw new GenerationException("ramltopojo does not support inheriting from "
                        + Utils.declarationType(typeDeclaration) + " name: " + typeDeclaration.name() + " and " + typeDeclaration.type());
            }
        }

        for (TypeDeclaration declaration : objectTypeDeclaration.properties()) {

            TypeName tn = findType(declaration.type(), declaration, generationContext);
            typeSpec.addMethod(MethodSpec.methodBuilder(Names.methodName("get", declaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(tn).build());

            if ( declaration.name().equals(discriminator.orNull())) {

                continue;
            }

            typeSpec.addMethod(MethodSpec.methodBuilder(Names.methodName("set", declaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(tn, Names.variableName(declaration.name())).build());

        }

        return typeSpec.build();
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {

        return TypeDeclarationType.javaType(typeName, type, generationContext);
    }
}
