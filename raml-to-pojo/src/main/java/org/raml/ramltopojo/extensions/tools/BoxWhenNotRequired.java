package org.raml.ramltopojo.extensions.tools;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class BoxWhenNotRequired implements ReferenceTypeHandlerPlugin {

    private final List<String> arguments;

    public BoxWhenNotRequired(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public TypeName typeName(ReferencePluginContext referencePluginContext, Shape ramlType, TypeName currentSuggestion) {

        if (/* TODO ! ramlType.required()*/ false) {
            return currentSuggestion.box();
        } else {
            return currentSuggestion;
        }
    }
}
