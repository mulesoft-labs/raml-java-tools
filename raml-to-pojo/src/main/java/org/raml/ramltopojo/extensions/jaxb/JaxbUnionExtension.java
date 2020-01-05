package org.raml.ramltopojo.extensions.jaxb;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.Shape;
import amf.client.model.domain.UnionShape;
import com.squareup.javapoet.*;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;

import javax.xml.bind.annotation.*;

/**
 * Created. There, you have it.
 */
public class JaxbUnionExtension implements UnionTypeHandlerPlugin {

    @Override
    public ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType) {
        return currentSuggestion;
    }

    @Override
    public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionShape type, TypeSpec.Builder builder, EventType eventType) {

        String namespace = type.xmlSerialization() != null && type.xmlSerialization().namespace() != null ? type.xmlSerialization().namespace().value() : "##default";
        String name = type.xmlSerialization() != null && type.xmlSerialization().name() != null ? type.xmlSerialization().name().value() : type.name().value();

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
    public FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionShape union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType) {

        AnnotationSpec.Builder elementsAnnotation = AnnotationSpec.builder(XmlElements.class);
        for (Shape typeDeclaration : union.anyOf()) {

            TypeName unionPossibility = context.unionClass((AnyShape) typeDeclaration).getJavaName(EventType.IMPLEMENTATION);

            elementsAnnotation.addMember("value",
                    "$L",
                    AnnotationSpec
                            .builder(XmlElement.class)
                            .addMember("name", "$S", typeDeclaration.name())
                            .addMember("type",
                                    "$T.class", unionPossibility
                                    )
                            .build());
        }

        anyType.addAnnotation(elementsAnnotation.build());

        return anyType;
    }

    @Override
    public FieldSpec.Builder fieldBuilt(UnionPluginContext context, AnyShape property, FieldSpec.Builder fieldSpec, EventType eventType) {
        return fieldSpec;
    }
}
