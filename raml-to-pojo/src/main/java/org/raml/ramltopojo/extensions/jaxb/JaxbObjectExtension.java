package org.raml.ramltopojo.extensions.jaxb;

import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created. There, you have it.
 */
public class JaxbObjectExtension extends ObjectTypeHandlerPlugin.Helper {

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape type, TypeSpec.Builder builder, EventType eventType) {

/*
        String namespace = type.xml() != null && type.xml().namespace() != null ? type.xml().namespace() : "##default";
        String name = type.xml() != null && type.xml().name() != null ? type.xml().name() : type.name();
*/

        String namespace =  "##default";
        String name = type.name().value();


        if (eventType == EventType.IMPLEMENTATION) {
            builder.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class)
                    .addMember("value", "$T.$L", XmlAccessType.class, "FIELD").build());

            AnnotationSpec.Builder annotation = AnnotationSpec.builder(XmlRootElement.class)
                    .addMember("namespace", "$S", namespace)
                    .addMember("name", "$S", name);

            builder.addAnnotation(annotation.build());
        } else {

            builder.addAnnotation(AnnotationSpec.builder(XmlRootElement.class)
                    .addMember("namespace", "$S", namespace)
                    .addMember("name", "$S", name).build());
        }

        return builder;
    }

    @Override
    public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape property, FieldSpec.Builder fieldSpec, EventType eventType) {

/*
        String namespace = type.xml() != null && type.xml().namespace() != null ? type.xml().namespace() : "##default";
        String name = type.xml() != null && type.xml().name() != null ? type.xml().name() : type.name();
*/

        String namespace =  "##default";
        String name = property.name().value();

/* TODO JP property.xml ?????????????????
        if (eventType == EventType.IMPLEMENTATION) {

            if (property.xml() != null && property.xml().wrapped() != null && property.xml().wrapped() && isArray(property)) {

                fieldSpec.addAnnotation(
                        AnnotationSpec.builder(XmlElementWrapper.class).addMember("name", "$S", name).build()
                );

                TypeName elementTypeName = objectPluginContext.dependentType(((ArrayShape)property).items()).getJavaName(EventType.IMPLEMENTATION);

                if (property.xml().attribute() != null && property.xml().attribute()) {
                    fieldSpec.addAnnotation(
                            AnnotationSpec.builder(XmlAttribute.class)
                                    .addMember("name", "$S", elementTypeName)
                                    .addMember("namespace", "$S", namespace)
                                    .build());
                } else {
                    fieldSpec.addAnnotation(
                            AnnotationSpec.builder(XmlElement.class)
                                    .addMember("name", "$S", elementTypeName)
                                    .addMember("namespace", "$S", namespace)
                                    .build());
                }
            } else {

                if (property.xml() != null && property.xml().attribute()) {
                    fieldSpec.addAnnotation(
                            AnnotationSpec.builder(XmlAttribute.class)
                                    .addMember("name", "$S", name)
                                    .addMember("namespace", "$S", namespace)
                                    .build());
                } else {
                    fieldSpec.addAnnotation(
                            AnnotationSpec.builder(XmlElement.class)
                                    .addMember("name", "$S", name)
                                    .addMember("namespace", "$S", namespace)
                                    .build());
                }
            }
        }
*/

        return fieldSpec;
    }

    private boolean isArray(PropertyShape property) {
        return property.range() instanceof ArrayShape;
    }
}
