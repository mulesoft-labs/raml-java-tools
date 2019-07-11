package org.raml.ramltopojo.extensions.tools;

import amf.client.model.domain.UnionShape;
import com.squareup.javapoet.ClassName;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.AllTypesPluginHelper;
import org.raml.ramltopojo.extensions.EnumerationPluginContext;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.UnionPluginContext;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class RepackagePlugin extends AllTypesPluginHelper {

    private final List<String> arguments;

    public RepackagePlugin(List<String> arguments) {
        this.arguments = arguments;
    }

    @Override
    public ClassName className(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {

        return makeContained(arguments.get(0),  currentSuggestion);
    }

    @Override
    public ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType) {
        return makeContained(arguments.get(0),  currentSuggestion);
    }

    @Override
    public ClassName className(EnumerationPluginContext enumerationPluginContext, TypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {
        return makeContained(arguments.get(0),  currentSuggestion);
    }

    private ClassName makeContained(String pack, ClassName currentSuggestion) {

        if ( currentSuggestion.simpleNames().size() > 1) {
            return ClassName.get(pack, currentSuggestion.simpleNames().get(0), currentSuggestion.simpleNames().subList(1, currentSuggestion.simpleNames().size()).toArray(new String[0]));
        } else {
            return ClassName.get(pack, currentSuggestion.simpleNames().get(0));
        }
    }

}
