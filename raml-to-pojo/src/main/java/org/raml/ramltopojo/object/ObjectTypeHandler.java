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

        CreationResult.Builder builder = CreationResult.builder();

        TypeSpec interfaceSpec = createInterface(builder, generationContext);
        TypeSpec implementationSpec = createImplementation(builder, interfaceSpec, generationContext);

        builder.withInterface(interfaceSpec).withImplementation(implementationSpec);
        return builder.build(generationContext);
    }

    private TypeSpec createImplementation(CreationResult.Builder builder, TypeSpec interfaceSpec, GenerationContext generationContext) {

        ClassName className = ClassName.get(generationContext.defaultPackage(), Names.typeName(objectTypeDeclaration.name(), "Impl"));
        TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .addSuperinterface(ClassName.bestGuess(interfaceSpec.name))
                .addModifiers(Modifier.PUBLIC);

        typeSpec = generationContext.pluginsForObjects().classCreated(objectTypeDeclaration, typeSpec, EventType.IMPLEMENTATION);
        if ( typeSpec == null ) {
            return null;
        }

        Optional<String> discriminator = Optional.fromNullable(objectTypeDeclaration.discriminator());

        for (TypeDeclaration propertyDeclaration : objectTypeDeclaration.properties()) {

            TypeName tn;
            if ( TypeDeclarationType.isNewInlineType(propertyDeclaration) ){

                CreationResult cr = builder.internalTypes.get(propertyDeclaration.name());
                tn = ClassName.bestGuess(cr.getInterface().name);

            }  else {

                tn = findType(propertyDeclaration.type(), propertyDeclaration, generationContext);
            }

            FieldSpec.Builder field = FieldSpec.builder(tn, Names.variableName(propertyDeclaration.name())).addModifiers(Modifier.PRIVATE);
            if ( propertyDeclaration.name().equals(discriminator.orNull())) {

                String discriminatorValue = Optional.fromNullable(objectTypeDeclaration.discriminatorValue()).or(objectTypeDeclaration.name());
                field.addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer(CodeBlock.builder().add("$S", discriminatorValue).build());

            }
            field = generationContext.pluginsForObjects().fieldBuilt(propertyDeclaration, field, EventType.IMPLEMENTATION);
            if ( field != null ) {
                typeSpec.addField(field.build());
            }

            MethodSpec.Builder getMethod = MethodSpec.methodBuilder(Names.methodName("get", propertyDeclaration.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(CodeBlock.builder().addStatement("return this." + Names.variableName(propertyDeclaration.name())).build())
                    .returns(tn);
            getMethod = generationContext.pluginsForObjects().getterBuilt(propertyDeclaration, getMethod, EventType.IMPLEMENTATION);
           if ( getMethod != null ) {
               typeSpec.addMethod(getMethod.build());
           }

            if ( propertyDeclaration.name().equals(discriminator.orNull())) {

                continue;
            }

            MethodSpec.Builder setMethod = MethodSpec.methodBuilder(Names.methodName("set", propertyDeclaration.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(CodeBlock.builder().addStatement("this." + Names.variableName(propertyDeclaration.name()) + " = " + Names.variableName(propertyDeclaration.name())).build())
                    .addParameter(tn, Names.variableName(propertyDeclaration.name()));
            setMethod = generationContext.pluginsForObjects().getterBuilt(propertyDeclaration, setMethod, EventType.IMPLEMENTATION);
            if ( setMethod != null ) {
                typeSpec.addMethod(setMethod.build());
            }
        }

        return typeSpec.build();
    }

    private TypeSpec createInterface(CreationResult.Builder builder, GenerationContext generationContext) {

        ClassName interf = ClassName.get(generationContext.defaultPackage(), Names.typeName(objectTypeDeclaration.name()));
        TypeSpec.Builder typeSpec = TypeSpec
                .interfaceBuilder(interf)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        typeSpec = generationContext.pluginsForObjects().classCreated(objectTypeDeclaration, typeSpec, EventType.INTERFACE);
        if ( typeSpec == null ) {
            return null;
        }

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

        for (TypeDeclaration propertyDeclaration : objectTypeDeclaration.properties()) {


            TypeName tn;
            if ( TypeDeclarationType.isNewInlineType(propertyDeclaration) ){

                CreationResult cr = TypeDeclarationType.typeHandler(propertyDeclaration).create(generationContext);
                builder.withInternalType(propertyDeclaration.name(), cr);
                tn = ClassName.bestGuess(cr.getInterface().name);
            }  else {

                tn = findType(propertyDeclaration.type(), propertyDeclaration, generationContext);
            }

            MethodSpec.Builder getMethod = MethodSpec.methodBuilder(Names.methodName("get", propertyDeclaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(tn);
            getMethod = generationContext.pluginsForObjects().getterBuilt(propertyDeclaration, getMethod, EventType.INTERFACE);

            if ( getMethod != null ) {
                typeSpec.addMethod(getMethod.build());

                if (propertyDeclaration.name().equals(discriminator.orNull())) {

                    continue;
                }
            }

            MethodSpec.Builder setMethod = MethodSpec.methodBuilder(Names.methodName("set", propertyDeclaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(tn, Names.variableName(propertyDeclaration.name()));
            setMethod = generationContext.pluginsForObjects().setterBuilt(propertyDeclaration, setMethod, EventType.INTERFACE);
            if ( setMethod != null ) {
                typeSpec.addMethod(setMethod.build());
            }

        }

        return typeSpec.build();
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {

        return TypeDeclarationType.javaType(typeName, type, generationContext);
    }
}
