package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.ClassName;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.AllTypesPluginHelper;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class RenamePlugin extends AllTypesPluginHelper {

    private final List<String> arguments;

    public RenamePlugin(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public ClassName className(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {

        return changeName(currentSuggestion, eventType);
    }

    private ClassName changeName(ClassName currentSuggestion, EventType type) {
        if ( arguments.size() == 0 ) {

            return currentSuggestion;
        }

        if ( arguments.size() == 1 ){

            if ( type == EventType.IMPLEMENTATION) {

                return ClassName.get(currentSuggestion.packageName(),  arguments.get(0) + "Impl");
            } else {

                return ClassName.get(currentSuggestion.packageName(),  arguments.get(0));
            }
        } else {

            if ( type == EventType.IMPLEMENTATION) {

                return ClassName.get(currentSuggestion.packageName(),  arguments.get(1));
            } else {

                return ClassName.get(currentSuggestion.packageName(),  arguments.get(0));
            }
        }
    }
}
