package org.raml.ramltopojo.extensions.tools;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class ChangeType implements ReferenceTypeHandlerPlugin {

    private final List<String> arguments;

    public ChangeType(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public TypeName typeName(ReferencePluginContext referencePluginContext, Shape ramlType, TypeName currentSuggestion) {

        if ( arguments.size() == 2 && arguments.get(1).equals("unbox")) {
            return ClassName.bestGuess(arguments.get(0)).unbox();
        } else {

            return ClassName.bestGuess(arguments.get(0));
        }
    }
}
