package org.raml.ramltopojo.object;

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

        return CreationResult.forType(interfaceSpec, implementationSpec);
    }

    private TypeSpec createImplementation(TypeSpec interfaceSpec, GenerationContext generationContext) {

        ClassName className = ClassName.get(generationContext.defaultPackage(), Names.typeName(objectTypeDeclaration.name(), "Impl"));
        TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .addSuperinterface(ClassName.bestGuess(interfaceSpec.name))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        for (TypeDeclaration declaration : objectTypeDeclaration.properties()) {

            TypeName tn = findType(declaration.type(), declaration, generationContext);

            FieldSpec.Builder field = FieldSpec.builder(tn, Names.variableName(declaration.name())).addModifiers(Modifier.PRIVATE);
            typeSpec.addField(field.build());

            MethodSpec.Builder getter = MethodSpec.methodBuilder(Names.methodName("get", declaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(tn);

            MethodSpec.Builder setter = MethodSpec.methodBuilder(Names.methodName("set", declaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(tn, Names.variableName(declaration.name()));

            typeSpec.addMethod(getter.build());
            typeSpec.addMethod(setter.build());
        }

        return typeSpec.build();
    }

    private TypeSpec createInterface(GenerationContext generationContext) {

        ClassName interf = ClassName.get(generationContext.defaultPackage(), Names.typeName(objectTypeDeclaration.name()));
        TypeSpec.Builder typeSpec = TypeSpec
                .interfaceBuilder(interf)
                .addModifiers(Modifier.PUBLIC);

        for (TypeDeclaration declaration : objectTypeDeclaration.properties()) {

            TypeName tn = findType(declaration.type(), declaration, generationContext);
            MethodSpec.Builder getter = MethodSpec.methodBuilder(Names.methodName("get", declaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(tn);

            MethodSpec.Builder setter = MethodSpec.methodBuilder(Names.methodName("set", declaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(tn, Names.variableName(declaration.name()));

            typeSpec.addMethod(getter.build());
            typeSpec.addMethod(setter.build());
        }

/*
        for (GType parentType : parentTypes) {

            if ("object".equals(parentType.name())) {

                continue;
            }

            typeSpec.addSuperinterface(parentType.defaultJavaTypeName(context.getModelPackage()));
        }
*/

        if (typeSpec == null) {
            return null;
        }

//        buildPropertiesForInterface(context, objectType, properties, typeSpec);
        return typeSpec.build();
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {

        return TypeDeclarationType.javaType(typeName, type, generationContext);
    }
}
