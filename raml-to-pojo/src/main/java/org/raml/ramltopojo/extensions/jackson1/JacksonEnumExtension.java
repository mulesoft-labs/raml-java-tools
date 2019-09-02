package org.raml.ramltopojo.extensions.jackson1;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.codehaus.jackson.annotate.JsonProperty;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.EnumerationTypeHandlerPlugin;

/**
 * Created. There, you have it.
 */
public class JacksonEnumExtension extends EnumerationTypeHandlerPlugin.Helper {

    @Override
    public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, String value, EventType eventType) {

        return incoming.addAnnotation(AnnotationSpec.builder(JsonProperty.class).addMember("value", "$S", value)
                    .build());
    }
}
