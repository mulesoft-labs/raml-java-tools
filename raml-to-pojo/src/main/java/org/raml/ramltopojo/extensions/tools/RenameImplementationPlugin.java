package org.raml.ramltopojo.extensions.tools;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.Shape;
import amf.client.model.domain.UnionShape;
import com.squareup.javapoet.ClassName;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.AllTypesPluginHelper;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContext;

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
    public ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType) {
        return changeName(currentSuggestion, eventType);
    }

    @Override
    public ClassName className(EnumerationPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType) {
        return changeName(currentSuggestion, eventType);
    }
    @Override
    public ClassName className(ObjectPluginContext objectPluginContext, NodeShape ramlType, ClassName currentSuggestion, EventType eventType) {

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
