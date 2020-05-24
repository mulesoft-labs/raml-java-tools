package org.raml.ramltopojo.extensions;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface XmlSchemaTypeHandlerPlugin {

    class Helper implements XmlSchemaTypeHandlerPlugin {

        @Override
        public ClassName typeName(XmlSchemaPluginContext referencePluginContext, Shape ramlType, ClassName currentSuggestion) {

            return currentSuggestion;
        }
    }


    ClassName typeName(XmlSchemaPluginContext referencePluginContext, Shape ramlType, ClassName currentSuggestion);

    class Composite implements XmlSchemaTypeHandlerPlugin {

        private final List<XmlSchemaTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<XmlSchemaTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public ClassName typeName(XmlSchemaPluginContext referencePluginContext, Shape ramlType, ClassName currentSuggestion) {
            for (XmlSchemaTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.typeName(referencePluginContext, ramlType, currentSuggestion);
            }

            return currentSuggestion;
        }
    }
}
