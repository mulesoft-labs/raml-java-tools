package org.raml.ramltopojo.extensions.jaxb;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.EnumerationTypeHandlerPlugin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Created. There, you have it.
 */
public class JaxbEnumExtension extends EnumerationTypeHandlerPlugin.Helper {

    @Override
    public TypeSpec.Builder classCreated(EnumerationPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType) {

        return incoming.addAnnotation(AnnotationSpec.builder(XmlEnum.class).build());
    }

    @Override
    public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, String value, EventType eventType) {

        return incoming.addAnnotation(AnnotationSpec.builder(XmlEnumValue.class).addMember("value", "$S", value)
                .build());
    }
}
