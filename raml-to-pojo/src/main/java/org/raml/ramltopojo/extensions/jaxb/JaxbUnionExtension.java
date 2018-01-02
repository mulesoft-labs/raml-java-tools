package org.raml.ramltopojo.extensions.jaxb;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.xml.bind.annotation.*;

/**
 * Created. There, you have it.
 */
public class JaxbUnionExtension implements UnionTypeHandlerPlugin {

    @Override
    public ClassName className(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {
        return currentSuggestion;
    }

    @Override
    public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionTypeDeclaration type, TypeSpec.Builder builder, EventType eventType) {

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
    public FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionTypeDeclaration union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType) {

        AnnotationSpec.Builder elementsAnnotation = AnnotationSpec.builder(XmlElements.class);
        for (TypeDeclaration typeDeclaration : union.of()) {

            elementsAnnotation.addMember("value",
                    "$L",
                    AnnotationSpec
                            .builder(XmlElement.class)
                            .addMember("name", "$S", typeDeclaration.name())
                            .addMember("type",
                                    "$T.class",context.creationResult().getJavaName(EventType.IMPLEMENTATION)
                                    )
                            .build());
        }

        anyType.addAnnotation(elementsAnnotation.build());

        return anyType;
    }
}
