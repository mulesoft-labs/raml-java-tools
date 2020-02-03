package org.raml.ramltopojo.object;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectPluginContextImpl;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created. There, you have it.
 */
public class ObjectTypeHandler implements TypeHandler {

    public static final String DISCRIMINATOR_TYPE_NAME = "_DISCRIMINATOR_TYPE_NAME";
    private final String name;
    private final ObjectTypeDeclaration objectTypeDeclaration;

    private static final ParameterizedTypeName ADDITIONAL_PROPERTIES_TYPE = ParameterizedTypeName.get(
            Map.class, String.class,
            Object.class);

    public ObjectTypeHandler(String name, ObjectTypeDeclaration objectTypeDeclaration) {
        this.name = name;
        this.objectTypeDeclaration = objectTypeDeclaration;
    }


    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {

        ObjectPluginContext context = new ObjectPluginContextImpl(generationContext, null);

        ObjectTypeHandlerPlugin plugin = generationContext.pluginsForObjects(Utils.allParents(objectTypeDeclaration, new ArrayList<>()).toArray(new TypeDeclaration[0]));
        ClassName className;
        if ( type == EventType.IMPLEMENTATION ) {
            className = generationContext.buildDefaultClassName(Names.typeName(name, "Impl"), EventType.IMPLEMENTATION);
        } else {

            className = generationContext.buildDefaultClassName(Names.typeName(name), EventType.INTERFACE);
        }

        return plugin.className(context, objectTypeDeclaration, className, type);
    }

    @Override
    public TypeName javaClassReference(GenerationContext generationContext, EventType type) {
        return javaClassName(generationContext, type);
    }

    @Override
    // TODO deal with null interface spec.
    public Optional<CreationResult> create(GenerationContext generationContext, CreationResult result) {

        // I need to createHandler an interface and an implementation.
        ObjectPluginContext context = new ObjectPluginContextImpl(generationContext, result);
        TypeSpec interfaceSpec = createInterface(context,  result, generationContext);
        TypeSpec implementationSpec = createImplementation(context,  result, generationContext);

        if ( interfaceSpec == null ) {

            return Optional.absent();
        } else {
            return Optional.of(result.withInterface(interfaceSpec).withImplementation(implementationSpec));
        }
    }

    private TypeSpec createImplementation(ObjectPluginContext objectPluginContext, CreationResult result, GenerationContext generationContext) {

        ClassName className = result.getJavaName(EventType.IMPLEMENTATION);
        TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .addSuperinterface(result.getJavaName(EventType.INTERFACE))
                .addModifiers(Modifier.PUBLIC);

        Optional<String> discriminator = Optional.fromNullable(objectTypeDeclaration.discriminator());        
        
        for (TypeDeclaration propertyDeclaration : objectTypeDeclaration.properties()) {

            if ( EcmaPattern.isSlashedPattern(propertyDeclaration.name())) {

                continue;
            }

            TypeName tn;
            if ( TypeDeclarationType.isNewInlineType(propertyDeclaration) ){

                CreationResult cr = result.internalType(propertyDeclaration.name());
                if (cr.getImplementation().isPresent()) {

                    // we need a special handling for property unions, they need to be added as inline types
                    if (propertyDeclaration instanceof UnionTypeDeclaration) {
                        TypeSpec.Builder innerTypeSpecImpl = cr.getImplementation().get().toBuilder();
                        typeSpec.addType(innerTypeSpecImpl.addModifiers(Modifier.PUBLIC, Modifier.STATIC).build());
                    }
                }
                tn = cr.getJavaName(EventType.INTERFACE);

            }  else {

                tn = findType(propertyDeclaration.type(), propertyDeclaration, generationContext, EventType.INTERFACE);
            }

            FieldSpec.Builder field = FieldSpec.builder(tn, Names.variableName(propertyDeclaration.name())).addModifiers(Modifier.PRIVATE);
            if ( propertyDeclaration.name().equals(discriminator.orNull())) {

                String discriminatorValue = Optional.fromNullable(objectTypeDeclaration.discriminatorValue()).or(objectTypeDeclaration.name());
                field.addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer(CodeBlock.builder().add("$L", DISCRIMINATOR_TYPE_NAME).build());

            }
            field = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).fieldBuilt(objectPluginContext, propertyDeclaration, field, EventType.IMPLEMENTATION);
            if ( field != null ) {
                typeSpec.addField(field.build());
            }

            MethodSpec.Builder getMethod = MethodSpec.methodBuilder(Names.methodName("get", propertyDeclaration.name()))
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(CodeBlock.builder().addStatement("return this." + Names.variableName(propertyDeclaration.name())).build())
                    .returns(tn);
            getMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).getterBuilt(objectPluginContext, propertyDeclaration, getMethod, EventType.IMPLEMENTATION);
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
            setMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).setterBuilt(objectPluginContext, propertyDeclaration, setMethod, EventType.IMPLEMENTATION);
            if ( setMethod != null ) {
                typeSpec.addMethod(setMethod.build());
            }
        }

        if ( objectTypeDeclaration.additionalProperties()) {

            handleAdditionalPropertiesImplementation(objectPluginContext, result, generationContext, typeSpec);
        }

        typeSpec = generationContext.pluginsForObjects(objectTypeDeclaration).classCreated(objectPluginContext, objectTypeDeclaration, typeSpec, EventType.IMPLEMENTATION);
        if ( typeSpec == null ) {
            return null;
        }

        return typeSpec.build();
    }

    private TypeSpec createInterface(ObjectPluginContext objectPluginContext, CreationResult result, GenerationContext generationContext) {

        ClassName interf = result.getJavaName(EventType.INTERFACE);
        TypeSpec.Builder typeSpec = TypeSpec
                .interfaceBuilder(interf)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        typeSpec = generationContext.pluginsForObjects(objectTypeDeclaration).classCreated(objectPluginContext, objectTypeDeclaration, typeSpec, EventType.INTERFACE);
        if ( typeSpec == null ) {
            return null;
        }

        Optional<String> discriminator = Optional.fromNullable(objectTypeDeclaration.discriminator());

        for (TypeDeclaration typeDeclaration : objectTypeDeclaration.parentTypes()) {

            if (typeDeclaration instanceof ObjectTypeDeclaration) {

                if (typeDeclaration.name().equals("object")) {
                    continue;
                }

                TypeName inherits = findType(typeDeclaration.name(), typeDeclaration, generationContext, EventType.INTERFACE);
                typeSpec.addSuperinterface(inherits);
            } else {

                throw new GenerationException("ramltopojo does not support inheriting from "
                        + Utils.declarationType(typeDeclaration) + " name: " + typeDeclaration.name() + " and " + typeDeclaration.type());
            }
        }

        for (TypeDeclaration propertyDeclaration : objectTypeDeclaration.properties()) {

            if ( EcmaPattern.isSlashedPattern(propertyDeclaration.name())) {

                continue;
            }

            TypeName tn = null;
            if ( TypeDeclarationType.isNewInlineType(propertyDeclaration) ) {

                // we need a special handling for property unions, they need to be added as inline types
                if (propertyDeclaration instanceof UnionTypeDeclaration) {

                    // Inline union naming: string | nil => StringNilUnion
                    CreationResult cr = TypeDeclarationType.createInlineType(interf, result.getJavaName(EventType.IMPLEMENTATION),
                        Names.typeName(propertyDeclaration.type(), "union"), propertyDeclaration, generationContext).get();
                    result.withInternalType(propertyDeclaration.name(), cr);
                    tn = cr.getJavaName(EventType.INTERFACE);

                    typeSpec.addType(cr.getInterface().toBuilder().addModifiers(Modifier.PUBLIC, Modifier.STATIC).build());

                } else {

                    Optional<CreationResult> cr = TypeDeclarationType.createInlineType(interf, result.getJavaName(EventType.IMPLEMENTATION),
                        Names.typeName(propertyDeclaration.name(), "type"), propertyDeclaration, generationContext);
                    if (cr.isPresent()) {
                        result.withInternalType(propertyDeclaration.name(), cr.get());
                        tn = cr.get().getJavaName(EventType.INTERFACE);
                    }
                }
            }  else {

                tn = findType(propertyDeclaration.type(), propertyDeclaration, generationContext, EventType.INTERFACE);
            }

            if (tn == null) {
                continue;
            }
            MethodSpec.Builder getMethod = MethodSpec.methodBuilder(Names.methodName("get", propertyDeclaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(tn);
            getMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).getterBuilt(objectPluginContext, propertyDeclaration, getMethod, EventType.INTERFACE);

            if ( getMethod != null ) {
                typeSpec.addMethod(getMethod.build());

                if (propertyDeclaration.name().equals(discriminator.orNull())) {

                    String discriminatorValue = Optional.fromNullable(objectTypeDeclaration.discriminatorValue()).or(objectTypeDeclaration.name());
                    typeSpec.addField(
                            FieldSpec.builder(String.class, DISCRIMINATOR_TYPE_NAME, Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                .initializer(CodeBlock.builder().add("$S", discriminatorValue).build()).build());

                    continue;
                }
            }

            MethodSpec.Builder setMethod = MethodSpec.methodBuilder(Names.methodName("set", propertyDeclaration.name()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(tn, Names.variableName(propertyDeclaration.name()));
            setMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).setterBuilt(objectPluginContext, propertyDeclaration, setMethod, EventType.INTERFACE);
            if ( setMethod != null ) {
                typeSpec.addMethod(setMethod.build());
            }

        }

        if ( objectTypeDeclaration.additionalProperties()) {

            handleAdditionalPropertiesInterface(objectPluginContext, result, generationContext, typeSpec);
        }

        return typeSpec.build();
    }

    private void handleAdditionalPropertiesInterface(ObjectPluginContext objectPluginContext, CreationResult result, GenerationContext generationContext, TypeSpec.Builder typeSpec) {

        MethodSpec.Builder getAdditionalProperties = MethodSpec.methodBuilder("getAdditionalProperties")
                .returns(ADDITIONAL_PROPERTIES_TYPE).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        MethodSpec.Builder getSpec = generationContext.pluginsForObjects(objectTypeDeclaration).additionalPropertiesGetterBuilt(objectPluginContext, getAdditionalProperties, EventType.INTERFACE);
        if ( getSpec != null ) {
            typeSpec.addMethod(getSpec.build());
        }

        MethodSpec.Builder setAdditionalProperties = MethodSpec
                .methodBuilder("setAdditionalProperties")
                .returns(TypeName.VOID)
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(String.class), "key").build())
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(Object.class), "value").build())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        MethodSpec.Builder setSpec = generationContext.pluginsForObjects(objectTypeDeclaration).additionalPropertiesSetterBuilt(objectPluginContext, setAdditionalProperties, EventType.INTERFACE);
        if ( setSpec != null ) {
            typeSpec.addMethod(setSpec.build());
        }
    }

    private void handleAdditionalPropertiesImplementation(ObjectPluginContext objectPluginContext, CreationResult result, GenerationContext generationContext, TypeSpec.Builder typeSpec) {

        TypeName newSpec = objectPluginContext.createSupportClass(
                buildSpecialMap());

        FieldSpec.Builder additionalPropertiesField = FieldSpec
                .builder(ADDITIONAL_PROPERTIES_TYPE, "additionalProperties", Modifier.PRIVATE)
                .initializer(
                        withProperties(newSpec, objectTypeDeclaration).build());

        FieldSpec.Builder fieldSpec = generationContext.pluginsForObjects(objectTypeDeclaration).additionalPropertiesFieldBuilt(objectPluginContext, additionalPropertiesField, EventType.IMPLEMENTATION);
        if ( fieldSpec != null ) {
            typeSpec.addField(fieldSpec.build());
        }

        MethodSpec.Builder getAdditionalProperties = MethodSpec.methodBuilder("getAdditionalProperties")
                .returns(ADDITIONAL_PROPERTIES_TYPE).addModifiers(Modifier.PUBLIC)
                .addCode("return additionalProperties;\n");

        MethodSpec.Builder getSpec = generationContext.pluginsForObjects(objectTypeDeclaration).additionalPropertiesGetterBuilt(objectPluginContext, getAdditionalProperties, EventType.IMPLEMENTATION);
        if ( getSpec != null ) {
            typeSpec.addMethod(getSpec.build());
        }

        MethodSpec.Builder setAdditionalProperties = MethodSpec
                .methodBuilder("setAdditionalProperties")
                .returns(TypeName.VOID)
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(String.class), "key").build())
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(Object.class), "value").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.builder().add("this.additionalProperties.put(key, value);\n").build());

        MethodSpec.Builder setSpec = generationContext.pluginsForObjects(objectTypeDeclaration).additionalPropertiesSetterBuilt(objectPluginContext, setAdditionalProperties, EventType.IMPLEMENTATION);
        if ( setSpec != null ) {
            typeSpec.addMethod(setSpec.build());
        }

    }

    private CodeBlock.Builder withProperties(TypeName newSpec, ObjectTypeDeclaration object) {

        List<TypeDeclaration> properties = FluentIterable.from(object.properties()).filter(new Predicate<TypeDeclaration>() {
            @Override
            public boolean apply(@Nullable TypeDeclaration property) {
                return property != null && EcmaPattern.isSlashedPattern(property.name()) && ! EcmaPattern.fromString(property.name()).asJavaPattern().isEmpty();
            }
        }).toList();

        if ( properties.size() == 0) {

            return CodeBlock.of("new $T()", newSpec).toBuilder();
        }

        CodeBlock.Builder cb = CodeBlock.builder().beginControlFlow("new $T()", newSpec).beginControlFlow("");
        for (TypeDeclaration typeDeclaration : object.properties()) {

            cb.addStatement("addAcceptedPattern($T.compile($S))", Pattern.class, EcmaPattern.fromString(typeDeclaration.name()).asJavaPattern());
        }
        return cb.endControlFlow().endControlFlow();
    }

    protected TypeSpec.Builder buildSpecialMap() {
        return TypeSpec.classBuilder("ExcludingMap")
                .superclass(ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(Object.class)))
                .addField(FieldSpec.builder(TypeName.LONG, "serialVersionUID")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                        .initializer("1L")
                        .build())
                .addField(FieldSpec.builder(ParameterizedTypeName.get(Set.class, Pattern.class), "additionalProperties")
                        .initializer(CodeBlock.builder().add(" new $T()", ParameterizedTypeName.get(HashSet.class, Pattern.class)).build())
                        .build())
                .addMethod(
                        MethodSpec.methodBuilder("put")
                                .addParameter(ClassName.get(String.class), "key")
                                .addParameter(ClassName.get(Object.class), "value")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(TypeName.OBJECT)
                                .beginControlFlow("if ( additionalProperties.size() == 0 ) ")
                                .addStatement("return super.put(key, value)")
                                .endControlFlow()
                                .beginControlFlow("else")
                                .addStatement("return setProperty(key, value)")
                                .endControlFlow()
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("putAll")
                                .addParameter(ParameterizedTypeName.get(ClassName.get(Map.class), WildcardTypeName.subtypeOf(String.class), WildcardTypeName.subtypeOf(Object.class)), "otherMap")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(TypeName.VOID)
                                .addCode(CodeBlock.builder()
                                        .beginControlFlow("if ( additionalProperties.size() == 0 ) ")
                                        .addStatement("super.putAll(otherMap)")
                                        .endControlFlow()
                                        .beginControlFlow("else")
                                        .beginControlFlow("for ( $T<? extends $T, ?> entry : otherMap.entrySet() )", Map.Entry.class, String.class)
                                        .addStatement("setProperty(entry.getKey(), entry.getValue())")
                                        .endControlFlow()
                                        .endControlFlow()
                                        .build())
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("addAcceptedPattern")
                                .addParameter(ClassName.get(Pattern.class), "pattern")
                                .addModifiers(Modifier.PROTECTED)
                                .returns(TypeName.VOID)
                                .addCode(CodeBlock.builder().addStatement("additionalProperties.add(pattern)").build())
                                .build())
                .addMethod(
                        MethodSpec.methodBuilder("setProperty")
                                .addParameter(ClassName.get(String.class), "key")
                                .addParameter(ClassName.get(Object.class), "value")
                                .addModifiers(Modifier.PRIVATE)
                                .returns(TypeName.OBJECT)
                                .beginControlFlow("if ( additionalProperties.size() == 0 ) ")
                                .addStatement("return super.put(key, value)")
                                .endControlFlow()
                                .beginControlFlow("else")
                                .beginControlFlow("for ( $T p : additionalProperties)", Pattern.class)
                                .beginControlFlow("if ( p.matcher(key).matches() )")
                                .addStatement("return super.put(key, value)")
                                .endControlFlow()
                                .endControlFlow()
                                .addStatement("throw new $T(\"property \" + key + \" is invalid according to RAML type\")", IllegalArgumentException.class)
                                .endControlFlow()
                                .build())

                .addModifiers(Modifier.PUBLIC);
    }

    private TypeName findType(String typeName, TypeDeclaration type, GenerationContext generationContext, EventType eventType) {

        return TypeDeclarationType.calculateTypeName(typeName, type, generationContext,eventType );
    }
}
