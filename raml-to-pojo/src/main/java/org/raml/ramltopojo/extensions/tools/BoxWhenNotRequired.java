package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

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
    public TypeName typeName(ReferencePluginContext referencePluginContext, TypeDeclaration ramlType, TypeName currentSuggestion) {

        if (! ramlType.required()) {
            return currentSuggestion.box();
        } else {
            return currentSuggestion;
        }
    }
}
