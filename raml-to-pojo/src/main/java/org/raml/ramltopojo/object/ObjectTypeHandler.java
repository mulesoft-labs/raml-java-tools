package org.raml.ramltopojo.object;

import amf.client.model.StrField;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectPluginContextImpl;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class ObjectTypeHandler implements TypeHandler {

    public static final String DISCRIMINATOR_TYPE_NAME = "_DISCRIMINATOR_TYPE_NAME";
    private final String name;
    private final NodeShape objectTypeDeclaration;

    public ObjectTypeHandler(String name, NodeShape objectTypeDeclaration) {
        this.name = name;
        this.objectTypeDeclaration = objectTypeDeclaration;
    }

    @Override
    public ClassName javaClassName(GenerationContext generationContext, EventType type) {

        ObjectPluginContext context = new ObjectPluginContextImpl(generationContext, null);

        ObjectTypeHandlerPlugin plugin = generationContext.pluginsForObjects(Utils.allParents(objectTypeDeclaration, new ArrayList<>()).toArray(new Shape[0]));
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
    public java.util.Optional<CreationResult> create(GenerationContext generationContext, CreationResult result) {

        // I need to createHandler an interface and an implementation.
        ObjectPluginContext context = new ObjectPluginContextImpl(generationContext, result);
        TypeSpec interfaceSpec = createInterface(context,  result, generationContext);
        TypeSpec implementationSpec = createImplementation(context,  result, generationContext);

        if ( interfaceSpec == null ) {

            return java.util.Optional.empty();
        } else {
            return java.util.Optional.of(result.withInterface(interfaceSpec).withImplementation(implementationSpec));
        }
    }

    private TypeSpec createImplementation(ObjectPluginContext objectPluginContext, CreationResult result, GenerationContext generationContext) {

       ClassName className = result.getJavaName(EventType.IMPLEMENTATION);
        TypeSpec.Builder typeSpec = TypeSpec
                .classBuilder(className)
                .addSuperinterface(result.getJavaName(EventType.INTERFACE))
                .addModifiers(Modifier.PUBLIC);

        Optional<String> discriminator = Optional.ofNullable(objectTypeDeclaration.discriminator()).map(StrField::value);

        for (PropertyShape propertyDeclaration : objectTypeDeclaration.properties()) {

            if ( EcmaPattern.isSlashedPattern(propertyDeclaration.name().value())) {

                continue;
            }

            TypeName tn;
            if ( ShapeType.isNewInlineType(propertyDeclaration.range()) ){

                CreationResult cr = result.internalType(propertyDeclaration.name().value());
                tn = cr.getJavaName(EventType.INTERFACE);

            }  else {

                tn = findType(propertyDeclaration.range().name().value(), propertyDeclaration, generationContext, EventType.INTERFACE);
            }

            FieldSpec.Builder field = FieldSpec.builder(tn, Names.variableName(propertyDeclaration.name().value())).addModifiers(Modifier.PRIVATE);
            if ( propertyDeclaration.name().value().equals(discriminator.orElse(null))) {

                /* TODO JP why isn't this used */
                String discriminatorValue = Optional.ofNullable(objectTypeDeclaration.discriminatorValue().value()).orElse(objectTypeDeclaration.name().value());
                field.addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer(CodeBlock.builder().add("$L", DISCRIMINATOR_TYPE_NAME).build());

            }
            field = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).fieldBuilt(objectPluginContext, propertyDeclaration, field, EventType.IMPLEMENTATION);
            if ( field != null ) {
                typeSpec.addField(field.build());
            }

            MethodSpec.Builder getMethod = MethodSpec.methodBuilder(Names.methodName("get", propertyDeclaration.name().value()))
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(CodeBlock.builder().addStatement("return this." + Names.variableName(propertyDeclaration.name().value())).build())
                    .returns(tn);
            getMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).getterBuilt(objectPluginContext, propertyDeclaration, getMethod, EventType.IMPLEMENTATION);
           if ( getMethod != null ) {
               typeSpec.addMethod(getMethod.build());
           }

            if ( propertyDeclaration.name().equals(discriminator.orElse(null))) {

                continue;
            }

            MethodSpec.Builder setMethod = MethodSpec.methodBuilder(Names.methodName("set", propertyDeclaration.name().value()))
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(CodeBlock.builder().addStatement("this." + Names.variableName(propertyDeclaration.name().value()) + " = " + Names.variableName(propertyDeclaration.name().value())).build())
                    .addParameter(tn, Names.variableName(propertyDeclaration.name().value()));
            setMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).setterBuilt(objectPluginContext, propertyDeclaration, setMethod, EventType.IMPLEMENTATION);
            if ( setMethod != null ) {
                typeSpec.addMethod(setMethod.build());
            }
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

        Optional<String> discriminator = Optional.ofNullable(objectTypeDeclaration.discriminator()).map(StrField::value);

        for (Shape typeDeclaration : objectTypeDeclaration.inherits()) {

            if (typeDeclaration instanceof NodeShape) {

                if (typeDeclaration.name().value().equals("object")) {
                    continue;
                }

                TypeName inherits = findType(typeDeclaration.name().value(), typeDeclaration, generationContext, EventType.INTERFACE);
                typeSpec.addSuperinterface(inherits);
            } else {

                throw new GenerationException("ramltopojo does not support inheriting from "
                        + Utils.declarationType(typeDeclaration) + " name: " + typeDeclaration.name() + " and " + typeDeclaration.name().value());
            }
        }

        for (PropertyShape propertyDeclaration : objectTypeDeclaration.properties()) {

            if ( EcmaPattern.isSlashedPattern(propertyDeclaration.name().value())) {

                continue;
            }

            TypeName tn = null;
            if ( ShapeType.isNewInlineType(propertyDeclaration) ){

                java.util.Optional<CreationResult> cr = CreationResultFactory.createInlineType(interf, result.getJavaName(EventType.IMPLEMENTATION),  Names.typeName(propertyDeclaration.name().value(), "type"), propertyDeclaration, generationContext);
                if ( cr.isPresent() ) {
                    result.withInternalType(propertyDeclaration.name().value(), cr.get());
                    tn = cr.get().getJavaName(EventType.INTERFACE);
                }
            }  else {

                tn = findType(propertyDeclaration.range().name().value(), propertyDeclaration, generationContext, EventType.INTERFACE);
            }

            if (tn == null) {
                continue;
            }
            MethodSpec.Builder getMethod = MethodSpec.methodBuilder(Names.methodName("get", propertyDeclaration.name().value()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(tn);
            getMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).getterBuilt(objectPluginContext, propertyDeclaration, getMethod, EventType.INTERFACE);

            if ( getMethod != null ) {
                typeSpec.addMethod(getMethod.build());

                if (propertyDeclaration.name().value().equals(discriminator.orElse(null))) {

                    String discriminatorValue = Optional.ofNullable(objectTypeDeclaration.discriminatorValue()).map(StrField::value).orElse(objectTypeDeclaration.name().value());
                    typeSpec.addField(
                            FieldSpec.builder(String.class, DISCRIMINATOR_TYPE_NAME, Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                .initializer(CodeBlock.builder().add("$S", discriminatorValue).build()).build());

                    continue;
                }
            }

            MethodSpec.Builder setMethod = MethodSpec.methodBuilder(Names.methodName("set", propertyDeclaration.name().value()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(tn, Names.variableName(propertyDeclaration.name().value()));
            setMethod = generationContext.pluginsForObjects(objectTypeDeclaration, propertyDeclaration).setterBuilt(objectPluginContext, propertyDeclaration, setMethod, EventType.INTERFACE);
            if ( setMethod != null ) {
                typeSpec.addMethod(setMethod.build());
            }

        }

        return typeSpec.build();
    }

    private TypeName findType(String typeName, PropertyShape type, GenerationContext generationContext, EventType eventType) {

        return ShapeType.calculateTypeName(typeName, type, generationContext,eventType );
    }

    private TypeName findType(String typeName, Shape type, GenerationContext generationContext, EventType eventType) {

        return ShapeType.calculateTypeName(typeName, type, generationContext,eventType );
    }

}
