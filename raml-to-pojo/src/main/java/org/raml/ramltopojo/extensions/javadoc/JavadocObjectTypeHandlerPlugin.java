package org.raml.ramltopojo.extensions.javadoc;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.Example;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;

/**
 * Created. There, you have it.
 */

public class JavadocObjectTypeHandlerPlugin extends ObjectTypeHandlerPlugin.Helper {

    private interface JavadocAdder {

        void addJavadoc(String format, Object... args);
    }

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, final TypeSpec.Builder incoming, EventType eventType) {

        if (ramlType.description() != null) {
            incoming.addJavadoc("$L\n", ramlType.description().value());
        }

        javadocExamples(incoming::addJavadoc, ramlType);

        return incoming;
    }

    @Override
    public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, final MethodSpec.Builder incoming, EventType eventType) {
        if (declaration.description() != null) {
            incoming.addJavadoc("$L\n", declaration.description().value());
        }

        javadocExamples(incoming::addJavadoc, (AnyShape) declaration.range());

        return incoming;
    }


    private void javadocExamples(JavadocAdder adder, AnyShape typeDeclaration) {

        for (Example exampleSpec : typeDeclaration.examples()) {
            javadoc(adder, exampleSpec);
        }
    }

    private void javadoc(JavadocAdder adder, Example exampleSpec) {
        adder.addJavadoc("Example:\n");

        if (exampleSpec.name() != null) {
            adder.addJavadoc(" $L\n", exampleSpec.name().value());
        }

        adder.addJavadoc(" $L\n", "<pre>\n{@code\n" + exampleSpec.value().value() + "\n}</pre>");
    }

}
