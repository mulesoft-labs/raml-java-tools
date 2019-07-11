package org.raml.ramltopojo.extensions;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ReferenceTypeHandlerPlugin {

    class Helper implements ReferenceTypeHandlerPlugin {

        @Override
        public TypeName typeName(ReferencePluginContext referencePluginContext, Shape ramlType, TypeName currentSuggestion) {

            return currentSuggestion;
        }
    }


    TypeName typeName(ReferencePluginContext referencePluginContext, Shape ramlType, TypeName currentSuggestion);

    class Composite implements ReferenceTypeHandlerPlugin {

        private final List<ReferenceTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<ReferenceTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public TypeName typeName(ReferencePluginContext referencePluginContext, Shape ramlType, TypeName currentSuggestion) {
            for (ReferenceTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.typeName(referencePluginContext, ramlType, currentSuggestion);
            }

            return currentSuggestion;
        }
    }
}
