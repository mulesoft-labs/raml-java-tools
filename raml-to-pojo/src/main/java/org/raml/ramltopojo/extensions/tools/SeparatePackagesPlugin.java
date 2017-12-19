package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.ClassName;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class SeparatePackagesPlugin extends ObjectTypeHandlerPlugin.Helper {

    private final List<String> arguments;

    public SeparatePackagesPlugin(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public ClassName className(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {

        if ( "".equals(currentSuggestion.packageName())) {
            return currentSuggestion;
        }

        if ( eventType == EventType.INTERFACE) {
            return ClassName.get(arguments.get(0), currentSuggestion.simpleName());
        } else {

            return ClassName.get(arguments.get(1), currentSuggestion.simpleName());
        }
    }
}
