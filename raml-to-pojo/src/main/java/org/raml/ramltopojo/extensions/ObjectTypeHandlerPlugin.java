package org.raml.ramltopojo.extensions;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ObjectTypeHandlerPlugin {



    class Helper implements ObjectTypeHandlerPlugin {

        @Override
        public ClassName className(ObjectPluginContext objectPluginContext, NodeShape ramlType, ClassName currentSuggestion, EventType eventType) {
            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, FieldSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public MethodSpec.Builder additionalPropertiesGetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType anInterface) {
            return incoming;
        }

        @Override
        public MethodSpec.Builder additionalPropertiesSetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType) {
            return incoming;
        }

        @Override
        public FieldSpec.Builder additionalPropertiesFieldBuilt(ObjectPluginContext objectPluginContext, FieldSpec.Builder incoming, EventType eventType) {
            return incoming;
        }
    }

    ClassName className(ObjectPluginContext objectPluginContext, NodeShape ramlType, ClassName currentSuggestion, EventType eventType);
    TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType);
    FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, FieldSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder additionalPropertiesGetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder additionalPropertiesSetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType);
    FieldSpec.Builder additionalPropertiesFieldBuilt(ObjectPluginContext objectPluginContext, FieldSpec.Builder incoming, EventType eventType);

    class Composite implements ObjectTypeHandlerPlugin {

        private final List<ObjectTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<ObjectTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public ClassName className(ObjectPluginContext objectPluginContext, NodeShape ramlType, ClassName currentSuggestion, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                currentSuggestion = plugin.className(objectPluginContext, ramlType, currentSuggestion, eventType);
            }

            return currentSuggestion;
        }

        @Override
        public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, NodeShape ramlType, TypeSpec.Builder incoming, EventType eventType) {

            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.classCreated(objectPluginContext, ramlType, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, FieldSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.fieldBuilt(objectPluginContext, declaration, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.getterBuilt(objectPluginContext, declaration, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, PropertyShape declaration, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.setterBuilt(objectPluginContext, declaration, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder additionalPropertiesGetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.additionalPropertiesGetterBuilt(objectPluginContext, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder additionalPropertiesSetterBuilt(ObjectPluginContext objectPluginContext, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.additionalPropertiesSetterBuilt(objectPluginContext, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public FieldSpec.Builder additionalPropertiesFieldBuilt(ObjectPluginContext objectPluginContext, FieldSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.additionalPropertiesFieldBuilt(objectPluginContext, incoming, eventType);
            }

            return incoming;
        }

    }
}
