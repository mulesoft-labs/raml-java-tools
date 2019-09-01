package org.raml.ramltopojo.object;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

/**
 * Created. There, you have it.
 */
public class TestPlugin extends ObjectTypeHandlerPlugin.Helper {

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {

        incoming.addAnnotation(Deprecated.class);
        return incoming;
    }

    @Override
    public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, FieldSpec.Builder incoming, EventType eventType) {
        return incoming;
    }

    @Override
    public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
        return incoming;
    }

    @Override
    public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
        return incoming;
    }
}
