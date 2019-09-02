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
public interface EnumerationTypeHandlerPlugin {

    class Helper implements EnumerationTypeHandlerPlugin {

        @Override
        public ClassName className(EnumerationPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType) {
            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(EnumerationPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, String value, EventType eventType) {
            return incoming;
        }
        @Override
        public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, Number value, EventType eventType) {
            return incoming;
        }
    }

    ClassName className(EnumerationPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType);
    TypeSpec.Builder classCreated(EnumerationPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType);
    TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder enumValue, String value, EventType eventType);
    TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder enumValue, Number value, EventType eventType);

    class Composite implements EnumerationTypeHandlerPlugin {

        private final List<EnumerationTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<EnumerationTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public ClassName className(EnumerationPluginContext enumerationPluginContext, Shape ramlType, ClassName currentSuggestion, EventType eventType) {
            for (EnumerationTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.className(enumerationPluginContext, ramlType, currentSuggestion, eventType);
            }

            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(EnumerationPluginContext enumerationPluginContext, Shape ramlType, TypeSpec.Builder incoming, EventType eventType) {

            for (EnumerationTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.classCreated(enumerationPluginContext, ramlType, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, String value, EventType eventType) {
            for (EnumerationTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.enumValue(enumerationPluginContext, declaration, incoming, value, eventType);
            }

            return incoming;
        }

        @Override
        public TypeSpec.Builder enumValue(EnumerationPluginContext enumerationPluginContext, Shape declaration, TypeSpec.Builder incoming, Number value, EventType eventType) {
            for (EnumerationTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.enumValue(enumerationPluginContext, declaration, incoming, value, eventType);
            }

            return incoming;
        }

    }
}
