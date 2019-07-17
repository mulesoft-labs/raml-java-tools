package org.raml.ramltopojo.extensions.jaxb;

import amf.client.model.domain.ArrayShape;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.xml.bind.annotation.*;

/**
 * Created. There, you have it.
 */
public class JaxbObjectExtension extends ObjectTypeHandlerPlugin.Helper {

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration type, TypeSpec.Builder builder, EventType eventType) {

        String namespace = type.xml() != null && type.xml().namespace() != null ? type.xml().namespace() : "##default";
        String name = type.xml() != null && type.xml().name() != null ? type.xml().name() : type.name();

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
    public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, TypeDeclaration property, FieldSpec.Builder fieldSpec, EventType eventType) {

        String namespace = property.xml() != null && property.xml().namespace() != null ? property.xml().namespace() : "##default";
        String name = property.xml() != null && property.xml().name() != null ? property.xml().name() : property.name();

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

        return fieldSpec;
    }

    private boolean isArray(TypeDeclaration property) {
        return property instanceof ArrayTypeDeclaration;
    }
}
