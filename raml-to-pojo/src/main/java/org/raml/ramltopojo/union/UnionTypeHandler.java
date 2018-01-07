package org.raml.ramltopojo.union;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContextImpl;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class UnionTypeHandler implements TypeHandler {

    private final String name;
    private final UnionTypeDeclaration union;

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
    public CreationResult create(GenerationContext generationContext, CreationResult preCreationResult) {

        UnionPluginContext context = new UnionPluginContextImpl(generationContext, preCreationResult);

        CreationResult result = generationContext.findCreatedType(union.name(), union);
        ClassName interfaceName = preCreationResult.getJavaName(EventType.INTERFACE);
        ClassName implementationName = preCreationResult.getJavaName(EventType.IMPLEMENTATION);

        TypeSpec.Builder interf = getDeclaration(interfaceName, generationContext, context);
        TypeSpec.Builder impl = getImplementation(interfaceName, implementationName, generationContext, context);
        return result.withInterface(interf.build()).withImplementation(impl.build());
    }

    private TypeSpec.Builder getImplementation(ClassName interfaceName, ClassName implementationName, GenerationContext generationContext, UnionPluginContext context) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(implementationName).addModifiers(Modifier.PUBLIC).addSuperinterface(interfaceName);
        typeSpec = generationContext.pluginsForUnions(union).classCreated(context, union, typeSpec, EventType.IMPLEMENTATION);
        if ( typeSpec == null ) {
            return null;
        }

        FieldSpec.Builder anyType = FieldSpec.builder(Object.class, "anyType", Modifier.PRIVATE);
        anyType = generationContext.pluginsForUnions(union).anyFieldCreated(context, union, typeSpec, anyType, EventType.IMPLEMENTATION);
        if ( anyType == null ) {

            return typeSpec;
        }

        typeSpec.addField(anyType.build());
        typeSpec.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE).addStatement("this.anyType = null")

                        .build());

        for (TypeDeclaration unitedType : union.of()) {

            TypeName typeName =  findType(unitedType.name(), unitedType, generationContext).box();
            String shortened = shorten(typeName);

            String fieldName = Names.methodName(unitedType.name());
            typeSpec
                    .addMethod(
                            MethodSpec.constructorBuilder()
                                    .addParameter(ParameterSpec.builder(typeName, fieldName).build())
                                    .addModifiers(Modifier.PUBLIC).addStatement("this.anyType = $L", fieldName)
                                    .build())
                    .addMethod(
                            MethodSpec
                                    .methodBuilder(Names.methodName("get", shortened))
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(typeName)
                                    .addStatement(
                                            "if ( !(anyType instanceof  $T)) throw new $T(\"fetching wrong type out of the union: $L\")",
                                            typeName, IllegalStateException.class, typeName)
                                    .addStatement("return ($T) anyType", typeName).build())
                    .addMethod(
                            MethodSpec.methodBuilder(Names.methodName("is", shortened))
                                    .addStatement("return anyType instanceof $T", typeName)
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(TypeName.BOOLEAN).build()
                    ).build();
        }
        return typeSpec;
    }

    private TypeSpec.Builder getDeclaration(ClassName interfaceName, final GenerationContext generationContext, UnionPluginContext context) {
        TypeSpec.Builder typeSpec = TypeSpec.interfaceBuilder(interfaceName).addModifiers(Modifier.PUBLIC);
        List<TypeName> names = FluentIterable.from(union.of()).transform(new Function<TypeDeclaration, TypeName>() {
            @Nullable
            @Override
            public TypeName apply(@Nullable TypeDeclaration unitedType) {
                return findType(unitedType.name(), unitedType, generationContext).box();
            }
        }).toList();

        typeSpec = generationContext.pluginsForUnions(union).classCreated(context, union, typeSpec, EventType.INTERFACE);
        if ( typeSpec == null ) {
            return null;
        }


        for (TypeName unitedType : names) {

            if ( unitedType instanceof ArrayTypeDeclaration ) {

                throw new GenerationException("ramltopojo currently does not support arrays in unions");
            }

            String shortened = shorten(unitedType);

            typeSpec
                    .addMethod(
                            MethodSpec
                                    .methodBuilder(Names.methodName("get", shortened))
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(unitedType).build())
                    .addMethod(
                            MethodSpec.methodBuilder(Names.methodName("is", shortened))
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(TypeName.BOOLEAN).build()
                    );
        }
        return typeSpec;
    }

    private String shorten(TypeName typeName) {

        if ( ! (typeName instanceof ClassName) ) {

            throw new GenerationException(typeName + toString() +  " cannot be shortened reasonably");
        } else {

            return ((ClassName)typeName).simpleName();
        }
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext) {

        return TypeDeclarationType.calculateTypeName(typeName, type, generationContext, EventType.INTERFACE);
    }
}
