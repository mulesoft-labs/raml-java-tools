package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.ClassName;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.AllTypesPluginHelper;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class RenameImplementationPlugin extends AllTypesPluginHelper {

    private final List<String> arguments;

    public RenameImplementationPlugin(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public ClassName className(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {
        return changeName(currentSuggestion, eventType);
    }

    @Override
    public ClassName className(EnumerationPluginContext enumerationPluginContext, StringTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {
        return changeName(currentSuggestion, eventType);
    }
    @Override
    public ClassName className(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {

        return changeName(currentSuggestion, eventType);
    }

    private ClassName changeName(ClassName currentSuggestion, EventType type) {
        if ( type == EventType.INTERFACE) {
            return currentSuggestion;
        } else {
            return ClassName.get(currentSuggestion.packageName(),  arguments.get(0));
        }
    }

}
