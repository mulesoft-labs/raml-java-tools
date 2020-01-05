package org.raml.ramltopojo.extensions;

import amf.client.model.domain.*;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;

/**
 * Created. There, you have it.
 */
public class AllTypesPluginHelper implements ObjectTypeHandlerPlugin, UnionTypeHandlerPlugin, EnumerationTypeHandlerPlugin, ArrayTypeHandlerPlugin {

    private final ObjectTypeHandlerPlugin.Helper objectTypeHandlerPlugin= new ObjectTypeHandlerPlugin.Helper();
    private final UnionTypeHandlerPlugin.Helper unionTypeHandlerPlugin= new UnionTypeHandlerPlugin.Helper();
    private final EnumerationTypeHandlerPlugin.Helper enumerationTypeHandlerPlugin= new EnumerationTypeHandlerPlugin.Helper();
    private final ArrayTypeHandlerPlugin.Helper arrayTypeHandlerPlugin = new ArrayTypeHandlerPlugin.Helper();

    @Override
    public ClassName className(ObjectPluginContext objectPluginContext, NodeShape ramlType, ClassName currentSuggestion, EventType eventType) {
        return objectTypeHandlerPlugin.className(objectPluginContext, ramlType, currentSuggestion, eventType);
    }

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {
        return objectTypeHandlerPlugin.classCreated(objectPluginContext, ramlType, incoming, eventType);
    }

    @Override
    public MethodSpec.Builder additionalPropertiesGetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType) {
        return objectTypeHandlerPlugin.additionalPropertiesGetterBuilt(objectPluginContext, incoming, eventType);
    }

    @Override
    public MethodSpec.Builder additionalPropertiesSetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType) {
        return objectTypeHandlerPlugin.additionalPropertiesSetterBuilt(objectPluginContext, incoming, eventType);
    }

    @Override
    public FieldSpec.Builder additionalPropertiesFieldBuilt(ObjectPluginContext objectPluginContext, FieldSpec.Builder incoming, EventType eventType) {
        return objectTypeHandlerPlugin.additionalPropertiesFieldBuilt(objectPluginContext, incoming, eventType);
    }

    @Override
    public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, FieldSpec.Builder incoming, EventType eventType) {
        return objectTypeHandlerPlugin.fieldBuilt(objectPluginContext, declaration, incoming, eventType);
    }

    @Override
    public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
        return objectTypeHandlerPlugin.getterBuilt(objectPluginContext, declaration, incoming, eventType);
    }

    @Override
    public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
        return objectTypeHandlerPlugin.setterBuilt(objectPluginContext, declaration, incoming, eventType);
    }

    @Override
    public ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType) {
        return unionTypeHandlerPlugin.className(unionPluginContext, ramlType, currentSuggestion, eventType);
    }

    @Override
    public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionShape ramlType, TypeSpec.Builder incoming, EventType eventType) {
        return unionTypeHandlerPlugin.classCreated(unionPluginContext, ramlType, incoming, eventType);
    }

    @Override
    public FieldSpec.Builder fieldBuilt(UnionPluginContext unionPluginContext, AnyShape ramlType, FieldSpec.Builder fieldSpec, EventType eventType) {
    	return unionTypeHandlerPlugin.fieldBuilt(unionPluginContext, ramlType, fieldSpec, eventType);
    }

    @Override
    public FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionShape union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType) {
        return unionTypeHandlerPlugin.anyFieldCreated(context, union, typeSpec, anyType, eventType);
    }

    @Override
    public ClassName className(EnumerationPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType) {
        return enumerationTypeHandlerPlugin.className(enumerationPluginContext, ramlType, currentSuggestion, eventType);
    }

    @Override
    public TypeSpec.Builder classCreated(EnumerationPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType) {
        return enumerationTypeHandlerPlugin.classCreated(enumerationPluginContext, ramlType, incoming, eventType);
    }

    @Override
    public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, String value, EventType eventType) {
        return enumerationTypeHandlerPlugin.enumValue(enumerationPluginContext, declaration, incoming, value, eventType);
    }

    @Override
    public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, Number value, EventType eventType) {
        return enumerationTypeHandlerPlugin.enumValue(enumerationPluginContext, declaration, incoming, value, eventType);
    }

    @Override
    public ClassName className(ArrayPluginContext arrayPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType) {
        return arrayTypeHandlerPlugin.className(arrayPluginContext, ramlType, currentSuggestion, eventType);
    }

    @Override
    public TypeSpec.Builder classCreated(ArrayPluginContext arrayPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType) {
        return arrayTypeHandlerPlugin.classCreated(arrayPluginContext, ramlType, incoming, eventType);
    }
}
