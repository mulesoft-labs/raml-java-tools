package org.raml.ramltopojo.extensions.jackson2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.EnumerationTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class JacksonEnumExtension extends EnumerationTypeHandlerPlugin.Helper {

    @Override
    public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, TypeDeclaration declaration, TypeSpec.Builder incoming, String value, EventType eventType) {

        return incoming.addAnnotation(AnnotationSpec.builder(JsonProperty.class).addMember("value", "$S", value)
                    .build());
    }
}
