package org.raml.ramltopojo.union;

import com.squareup.javapoet.*;
import org.raml.ramltopojo.*;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.lang.model.element.Modifier;

/**
 * Created. There, you have it.
 */
public class UnionTypeHandler implements TypeHandler {

    private final UnionTypeDeclaration union;

    public UnionTypeHandler(UnionTypeDeclaration union) {

        this.union = union;
    }

    @Override
    public CreationResult create(GenerationContext generationContext) {

        ClassName interfaceName = ClassName.get(generationContext.defaultPackage(), Names.typeName(union.name()));
        ClassName implementationName = ClassName.get(generationContext.defaultPackage(), Names.typeName(union.name(), "Impl"));

        TypeSpec.Builder interf = getDeclaration(interfaceName, generationContext);
        TypeSpec.Builder impl = getImplementation(interfaceName, implementationName, generationContext);
        return CreationResult.forType(generationContext.defaultPackage(), interf.build(), impl.build());
    }

    private TypeSpec.Builder getImplementation(ClassName interfaceName, ClassName implementationName, GenerationContext generationContext) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(implementationName).addModifiers(Modifier.PUBLIC).addSuperinterface(interfaceName);

        FieldSpec.Builder anyType = FieldSpec.builder(Object.class, "anyType", Modifier.PRIVATE);
        typeSpec.addField(anyType.build());
        typeSpec.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE).addStatement("this.anyType = null")

                        .build());

        for (TypeDeclaration unitedType : union.of()) {

            TypeName typeName = findType(unitedType.name(), unitedType, generationContext);

            String fieldName = Names.methodName(unitedType.name());
            typeSpec
                    .addMethod(
                            MethodSpec.constructorBuilder()
                                    .addParameter(ParameterSpec.builder(typeName, fieldName).build())
                                    .addModifiers(Modifier.PUBLIC).addStatement("this.anyType = $L", fieldName)
                                    .build())
                    .addMethod(
                            MethodSpec
                                    .methodBuilder(Names.methodName("get", unitedType.name()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(typeName)
                                    .addStatement(
                                            "if ( !(anyType instanceof  $T)) throw new $T(\"fetching wrong type out of the union: $T\")",
                                            typeName, IllegalStateException.class, typeName)
                                    .addStatement("return ($T) anyType", typeName).build())
                    .addMethod(
                            MethodSpec.methodBuilder(Names.methodName("is", unitedType.name()))
                                    .addStatement("return anyType instanceof $T", typeName)
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.BOOLEAN).build()
                    ).build();
        }
        return typeSpec;
    }

    private TypeSpec.Builder getDeclaration(ClassName interfaceName, GenerationContext generationContext) {
        TypeSpec.Builder typeSpec = TypeSpec.interfaceBuilder(interfaceName).addModifiers(Modifier.PUBLIC);

        for (TypeDeclaration unitedType : union.of()) {

            TypeName typeName = findType(unitedType.name(), unitedType, generationContext);

            typeSpec
                    .addMethod(
                            MethodSpec
                                    .methodBuilder(Names.methodName("get", unitedType.name()))
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(typeName).build())
                    .addMethod(
                            MethodSpec.methodBuilder(Names.methodName("is", unitedType.name()))
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(TypeName.BOOLEAN).build()
                    );
        }
        return typeSpec;
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {

        return TypeDeclarationType.javaType(typeName, type, generationContext);
    }
}
