package org.raml.ramltopojo.extensions;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.UnionShape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface UnionTypeHandlerPlugin {


    class Helper implements UnionTypeHandlerPlugin {

        @Override
        public ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType) {
            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionShape ramlType, TypeSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionShape union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType) {
            return anyType;
        }

        @Override
        public FieldSpec.Builder fieldBuilt(UnionPluginContext context, AnyShape ramlType, FieldSpec.Builder fieldSpec, EventType eventType) {
            return fieldSpec;
        }
    }

    ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType);
    TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionShape ramlType, TypeSpec.Builder incoming, EventType eventType);
    FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionShape union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType);
    FieldSpec.Builder fieldBuilt(UnionPluginContext unionPluginContext, AnyShape ramlType, FieldSpec.Builder fieldSpec, EventType eventType);

    class Composite implements UnionTypeHandlerPlugin {

        private final List<UnionTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<UnionTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public ClassName className(UnionPluginContext unionPluginContext, UnionShape ramlType, ClassName currentSuggestion, EventType eventType) {
            for (UnionTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.className(unionPluginContext, ramlType, currentSuggestion, eventType);
            }

            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(UnionPluginContext unionPluginContext, UnionShape ramlType, TypeSpec.Builder incoming, EventType eventType) {

            for (UnionTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.classCreated(unionPluginContext, ramlType, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public FieldSpec.Builder anyFieldCreated(UnionPluginContext context, UnionShape union, TypeSpec.Builder typeSpec, FieldSpec.Builder anyType, EventType eventType) {
            for (UnionTypeHandlerPlugin plugin : plugins) {
                if ( anyType == null ) {
                    break;
                }
                anyType = plugin.anyFieldCreated(context, union, typeSpec, anyType, eventType);
            }

            return anyType;
        }

        @Override
        public FieldSpec.Builder fieldBuilt(UnionPluginContext context, AnyShape ramlType, FieldSpec.Builder fieldSpec, EventType eventType) {
            for (UnionTypeHandlerPlugin plugin : plugins) {
                if (fieldSpec == null) {
                    break;
                }
                fieldSpec = plugin.fieldBuilt(context, ramlType, fieldSpec, eventType);
            }
            return fieldSpec;
        }
    }
}
