package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

/**
 * Created. There, you have it.
 */
public class MakeAbstract  extends ObjectTypeHandlerPlugin.Helper{

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {

        if ( eventType != EventType.INTERFACE ) {
            return null;
        }

        return incoming;
    }
}
