package org.raml.ramltopojo.extensions;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface UnionTypeHandlerPlugin {


    class Helper implements UnionTypeHandlerPlugin {

        @Override
        public ClassName className(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {
            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionTypeDeclaration union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType) {
            return anyType;
        }
    }

    ClassName className(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType);
    TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType);
    FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionTypeDeclaration union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType);

    class Composite implements UnionTypeHandlerPlugin {

        private final List<UnionTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<UnionTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public ClassName className(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, ClassName currentSuggestion, EventType eventType) {
            for (UnionTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.className(unionPluginContext, ramlType, currentSuggestion, eventType);
            }

            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {

            for (UnionTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.classCreated(unionPluginContext, ramlType, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionTypeDeclaration union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType) {
            for (UnionTypeHandlerPlugin plugin : plugins) {
                if ( anyType == null ) {
                    break;
                }
                anyType = plugin.anyFieldCreated(context, union, typeSpec, anyType, eventType);
            }

            return anyType;
        }
    }
}
