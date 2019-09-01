package org.raml.ramltopojo.plugin;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class PluginMoreOne extends ObjectTypeHandlerPlugin.Helper {

    private final List<String> arguments;

    public PluginMoreOne(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {
        return null;
    }

    @Override
    public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, FieldSpec.Builder incoming, EventType eventType) {
        return null;
    }

    @Override
    public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
        return null;
    }

    @Override
    public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
        return null;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
