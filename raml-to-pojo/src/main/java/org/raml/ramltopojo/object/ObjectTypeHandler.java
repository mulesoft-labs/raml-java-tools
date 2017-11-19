package org.raml.ramltopojo.object;

import com.squareup.javapoet.*;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.Names;
import org.raml.ramltopojo.TypeDeclarationType;
import org.raml.ramltopojo.TypeHandler;
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
    public CreationResult create() {

        // I need to create an interface and an implementation.

        TypeSpec interfaceSpec = createInterface();
        TypeSpec implementationSpec = createImplementation(interfaceSpec);

        return CreationResult.forType(interfaceSpec, implementationSpec);
    }

    private TypeSpec createImplementation(TypeSpec interfaceSpec) {

        ClassName className = ClassName.get("", Names.typeName(objectTypeDeclaration.name(), "Impl"));
        TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .superclass(ClassName.bestGuess(interfaceSpec.name))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        for (TypeDeclaration declaration : objectTypeDeclaration.properties()) {

            TypeName tn = findType(declaration);

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

    private TypeSpec createInterface() {

        ClassName interf = ClassName.get("", Names.typeName(objectTypeDeclaration.name()));
        TypeSpec.Builder typeSpec = TypeSpec
                .interfaceBuilder(interf)
                .addModifiers(Modifier.PUBLIC);

        for (TypeDeclaration declaration : objectTypeDeclaration.properties()) {

            TypeName tn = findType(declaration);
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

    private TypeName findType(TypeDeclaration type) {

        return TypeDeclarationType.declarationType(type).asJavaPoetType();
    }
}
