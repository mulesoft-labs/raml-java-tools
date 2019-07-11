package org.raml.ramltopojo.extensions;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ArrayTypeHandlerPlugin {

    class Helper implements ArrayTypeHandlerPlugin {

        @Override
        public ClassName className(ArrayPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType) {
            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(ArrayPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType) {
            return incoming;
        }
    }

    ClassName className(ArrayPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType);
    TypeSpec.Builder classCreated(ArrayPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType);

    class Composite implements ArrayTypeHandlerPlugin {

        private final List<ArrayTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<ArrayTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public ClassName className(ArrayPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType) {
            for (ArrayTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.className(enumerationPluginContext, ramlType, currentSuggestion, eventType);
            }

            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(ArrayPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType) {

            for (ArrayTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.classCreated(enumerationPluginContext, ramlType, incoming, eventType);
            }

            return incoming;
        }
    }
}
