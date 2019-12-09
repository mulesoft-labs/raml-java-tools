package org.raml.ramltopojo.union;

import com.google.common.base.Optional;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContextImpl;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NullTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.stream.Collectors;
/**
 * Created. There, you have it.
 */
public class UnionTypeHandler implements TypeHandler {

    private final String name;
    private final UnionTypeDeclaration union;
    public static final ClassName NULL_CLASS = ClassName.get(Object.class);

    public UnionTypeHandler(String name, UnionTypeDeclaration union) {
        this.name = name;
        this.union = union;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {

        UnionPluginContext context = new UnionPluginContextImpl(generationContext, null);

        UnionTypeHandlerPlugin plugin = generationContext.pluginsForUnions(Utils.allParents(union, new ArrayList<TypeDeclaration>()).toArray(new TypeDeclaration[0]));
        ClassName className;
        if ( type == EventType.IMPLEMENTATION ) {
            className = generationContext.buildDefaultClassName(Names.typeName(name, "Impl"), EventType.IMPLEMENTATION);
        } else {

            className = generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE);
        }

        return plugin.className(context, union, className, type);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        return javaClassName(generationContext, type);
    }

    @Override
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult preCreationResult) {

        UnionPluginContext context = new UnionPluginContextImpl(generationContext, preCreationResult);

        ClassName interfaceName = preCreationResult.getJavaName(EventType.INTERFACE);

        TypeSpec.Builder interf = getDeclaration(generationContext, context, preCreationResult);
        TypeSpec.Builder impl = getImplementation(interfaceName, generationContext, context, preCreationResult);

        if ( interf == null ) {

            return Optional.absent();
        } else {
            return Optional.of(preCreationResult.withInterface(interf.build()).withImplementation(impl.build()));
        }
    }

    private TypeSpec.Builder getImplementation(ClassName interfaceName, GenerationContext generationContext, UnionPluginContext context, CreationResult preCreationResult) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(preCreationResult.getJavaName(EventType.IMPLEMENTATION))
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(interfaceName);

        // check if union is ambiguous (duplicate primitive types could't be handled by constructor)
        if (UnionTypesHelper.isAmbiguous(union.of(), (x) -> findType(x.name(), x, generationContext))) {
            throw new GenerationException(
                "This union is ambiguous. It's impossible to create a correct constructor for ambiguous types: "
                    + union.of().stream().map(x -> findType(x.name(), x, generationContext)).collect(Collectors.toList())
                    + ". Use unique primitive types or classes with discriminator to solve this conflict."
            );
        }

        // add enum type field
        String unionEnumName = "unionType";
        ClassName unionClassInterfaceName = preCreationResult.getJavaName(EventType.INTERFACE);
        ClassName unionEnumClassName = unionClassInterfaceName.nestedClass(Names.typeName(unionEnumName));
        typeSpec.addField(FieldSpec.builder(unionEnumClassName, unionEnumName, Modifier.PRIVATE).build());

        // build empty constructor
        typeSpec.addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PROTECTED)
            .build());

        // build object constructor
        MethodSpec.Builder constructorSpec = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get(Object.class), "value");
        boolean firstConstructorArgument = true;

        // build enum getter
        typeSpec.addMethod(MethodSpec.methodBuilder(Names.methodName("get", unionEnumName))
            .addModifiers(Modifier.PUBLIC)
            .addStatement("return this.$L", unionEnumName)
            .returns(unionEnumClassName)
            .build());

        // add union members
        for (TypeDeclaration unitedType : UnionTypesHelper.sortByPriority(union.of())) {

            TypeName typeName = unitedType instanceof NullTypeDeclaration ? NULL_CLASS : findType(unitedType.name(), unitedType, generationContext).box();
            String prettyName = prettyName(unitedType, generationContext);

            String fieldName = Names.methodName(prettyName, "value");
            if (typeName == NULL_CLASS) {

                if (firstConstructorArgument) {
                    constructorSpec.beginControlFlow("if (value == null)");
                    firstConstructorArgument = false;
                } else {
                    constructorSpec.beginControlFlow("else if (value == null)");
                }
                constructorSpec.addStatement("this.$L = $T.NIL", unionEnumName, unionEnumClassName);
                constructorSpec.endControlFlow();

                typeSpec
                    .addMethod(MethodSpec.methodBuilder("isNil")
                        .addStatement("return this.$L == $T.NIL", unionEnumName, unionEnumClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .build())
                    .addMethod(MethodSpec.methodBuilder("getNil")
                        .addModifiers(Modifier.PUBLIC).returns(typeName)
                        .addStatement("if (!isNil()) throw new $T(\"fetching wrong type out of the union: NullType should be null\")", IllegalStateException.class)
                        .addStatement("return null")
                        .build())
                .build();

            } else {

                // Add value field with annotations (for every valid union member)
                // This is necessary to support field validations in unions
                FieldSpec.Builder fieldValueSpec = FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE);
                fieldValueSpec = generationContext.pluginsForUnions(union).fieldBuilt(context, unitedType, fieldValueSpec, EventType.IMPLEMENTATION);
                typeSpec.addField(fieldValueSpec.build());

                String enumName = Names.constantName(prettyName);
                String isName = Names.methodName("is", prettyName);
                String getName = Names.methodName("get", prettyName);

                // add constructor section
                if (firstConstructorArgument) {
                    constructorSpec.beginControlFlow("if (value instanceof $T)", typeName);
                    firstConstructorArgument = false;
                } else {
                    constructorSpec.beginControlFlow("else if (value instanceof $T)", typeName);
                }
                constructorSpec.addStatement("this.$L = $T.$L", unionEnumName, unionEnumClassName, enumName);
                constructorSpec.addStatement("this.$L = ($T) value", fieldName, typeName);
                constructorSpec.endControlFlow();

                // Add isX and getX
                typeSpec
                    .addMethod(MethodSpec.methodBuilder(isName)
                        .addStatement("return this.$L == $T.$L", unionEnumName, unionEnumClassName, enumName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .build())
                    .addMethod(MethodSpec.methodBuilder(getName)
                        .addModifiers(Modifier.PUBLIC).returns(typeName)
                        .addStatement("if (!$L()) throw new $T(\"fetching wrong type out of the union: $L\")", isName, IllegalStateException.class, typeName)
                        .addStatement("return this.$L", fieldName)
                        .build())
                    .build();
            }
        }

        // add unknown type exception to constructor spec and build constructor
        constructorSpec.beginControlFlow("else");
        constructorSpec.addStatement("throw new $T($S + value)", IllegalArgumentException.class, "Union creation is not supported for given value: ");
        constructorSpec.endControlFlow();
        typeSpec.addMethod(constructorSpec.build());

        typeSpec = generationContext.pluginsForUnions(union).classCreated(context, union, typeSpec, EventType.IMPLEMENTATION);
        if (typeSpec == null) {
            return null;
        }

        return typeSpec;
    }

    private TypeSpec.Builder getDeclaration(final GenerationContext generationContext, UnionPluginContext context, CreationResult preCreationResult) {

        ClassName unionClassName = preCreationResult.getJavaName(EventType.INTERFACE);
        TypeSpec.Builder typeSpec = TypeSpec.interfaceBuilder(unionClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        typeSpec = generationContext.pluginsForUnions(union).classCreated(context, union, typeSpec, EventType.INTERFACE);
        if (typeSpec == null) {
            return null;
        }

        // add enum for all types of union for easy switch statements
        String unionEnumName = "unionType";
        ClassName unionEnumClassName = unionClassName.nestedClass(Names.typeName(unionEnumName));
        TypeSpec.Builder enumTypeSpec = TypeSpec.enumBuilder(unionEnumClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        // build enum getter
        typeSpec.addMethod(MethodSpec.methodBuilder(Names.methodName("get", unionEnumName))
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(unionEnumClassName)
            .build());

        for (TypeDeclaration unitedType : union.of()) {

        if (unitedType instanceof ArrayTypeDeclaration) {
            throw new GenerationException("ramltopojo currently does not support arrays in unions");
        }

        TypeName typeName = unitedType instanceof NullTypeDeclaration ? NULL_CLASS: findType(unitedType.name(), unitedType, generationContext).box();
        String prettyName = prettyName(unitedType, generationContext);

        // add enum name
        enumTypeSpec.addEnumConstant(Names.constantName(prettyName));

        if (typeName == NULL_CLASS) {

            typeSpec
                .addMethod(MethodSpec.methodBuilder("isNil")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(TypeName.BOOLEAN)
                    .build())
                .addMethod(MethodSpec.methodBuilder("getNil")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeName)
                    .build());

        } else {

            typeSpec
                .addMethod(MethodSpec.methodBuilder(Names.methodName("is", prettyName))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(TypeName.BOOLEAN)
                    .build())
                .addMethod(MethodSpec.methodBuilder(Names.methodName("get", prettyName))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(typeName)
                    .build());
        }
        }

        // set enum as inner type
        typeSpec.addType(enumTypeSpec.build());

        return typeSpec;
    }

    private String prettyName(TypeDeclaration type, GenerationContext generationContext) {
        if (type.type() == null) {
            return type instanceof NullTypeDeclaration ? "nil" : shorten(findType(type.name(), type, generationContext).box());
        } else {
            return type.name();
        }
    }

    private String shorten(TypeName typeName) {
        if (!(typeName instanceof ClassName)) {
            throw new GenerationException(typeName + toString() + " cannot be shortened reasonably");
        } else {
            return ((ClassName) typeName).simpleName();
        }
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {
        return TypeDeclarationType.calculateTypeName(typeName, type, generationContext, EventType.INTERFACE);
    }
}
