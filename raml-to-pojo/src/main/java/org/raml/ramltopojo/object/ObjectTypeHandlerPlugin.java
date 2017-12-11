package org.raml.ramltopojo.object;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.PluginContext;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ObjectTypeHandlerPlugin {

    class Helper implements ObjectTypeHandlerPlugin {

        @Override
        public TypeSpec.Builder classCreated(PluginContext pluginContext, ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public FieldSpec.Builder fieldBuilt(PluginContext pluginContext, TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public MethodSpec.Builder getterBuilt(PluginContext pluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public MethodSpec.Builder setterBuilt(PluginContext pluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
            return incoming;
        }
    }

    TypeSpec.Builder classCreated(PluginContext pluginContext, ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType);
    FieldSpec.Builder fieldBuilt(PluginContext pluginContext, TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder getterBuilt(PluginContext pluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder setterBuilt(PluginContext pluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType);

    class Composite implements ObjectTypeHandlerPlugin {

        private final List<ObjectTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<ObjectTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public TypeSpec.Builder classCreated(PluginContext pluginContext, ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {

            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.classCreated(pluginContext, ramlType, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public FieldSpec.Builder fieldBuilt(PluginContext pluginContext, TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.fieldBuilt(pluginContext, declaration, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder getterBuilt(PluginContext pluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.getterBuilt(pluginContext, declaration, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder setterBuilt(PluginContext pluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.setterBuilt(pluginContext, declaration, incoming, eventType);
            }

            return incoming;
        }
    }
}
