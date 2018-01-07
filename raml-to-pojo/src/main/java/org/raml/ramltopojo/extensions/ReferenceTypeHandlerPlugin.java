package org.raml.ramltopojo.extensions;

import com.squareup.javapoet.TypeName;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ReferenceTypeHandlerPlugin {

    class Helper implements ReferenceTypeHandlerPlugin {

        @Override
        public TypeName typeName(ReferencePluginContext referencePluginContext, TypeDeclaration ramlType, TypeName currentSuggestion) {

            return currentSuggestion;
        }
    }


    TypeName typeName(ReferencePluginContext referencePluginContext, TypeDeclaration ramlType, TypeName currentSuggestion);

    class Composite implements ReferenceTypeHandlerPlugin {

        private final List<ReferenceTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<ReferenceTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public TypeName typeName(ReferencePluginContext referencePluginContext, TypeDeclaration ramlType, TypeName currentSuggestion) {
            for (ReferenceTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.typeName(referencePluginContext, ramlType, currentSuggestion);
            }

            return currentSuggestion;
        }
    }
}
