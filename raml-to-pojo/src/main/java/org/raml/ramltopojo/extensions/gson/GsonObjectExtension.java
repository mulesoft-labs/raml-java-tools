package org.raml.ramltopojo.extensions.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class GsonObjectExtension extends ObjectTypeHandlerPlugin.Helper{

    @Override
    public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType) {
        return incoming
                .addAnnotation(
                        AnnotationSpec.builder(SerializedName.class)
                                .addMember("value", "$S", objectPluginContext.creationResult().getJavaName(EventType.IMPLEMENTATION)).build())
                .addAnnotation(AnnotationSpec.builder(Expose.class).build());
    }
}
