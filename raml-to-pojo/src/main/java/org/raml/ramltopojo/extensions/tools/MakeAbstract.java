package org.raml.ramltopojo.extensions.tools;

import amf.client.model.domain.NodeShape;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

/**
 * Created. There, you have it.
 */
public class MakeAbstract  extends ObjectTypeHandlerPlugin.Helper{

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {

        if ( eventType != EventType.INTERFACE ) {
            return null;
        }

        return incoming;
    }
}
