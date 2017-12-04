package org.raml.ramltopojo.object;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ObjectTypeHandlerPlugin {

    TypeSpec.Builder classCreated(ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType);
    FieldSpec.Builder fieldBuilt(TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder getterBuilt(TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType);
    MethodSpec.Builder setterBuilt(TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType);

    class Composite implements ObjectTypeHandlerPlugin {

        private final List<ObjectTypeHandlerPlugin> plugins = new ArrayList<>();

        public Composite(Set<ObjectTypeHandlerPlugin> actualPlugins) {

            plugins.addAll(actualPlugins);
        }

        @Override
        public TypeSpec.Builder classCreated(ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {

            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.classCreated(ramlType, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public FieldSpec.Builder fieldBuilt(TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.fieldBuilt(declaration, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder getterBuilt(TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.getterBuilt(declaration, incoming, eventType);
            }

            return incoming;
        }

        @Override
        public MethodSpec.Builder setterBuilt(TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
            for (ObjectTypeHandlerPlugin plugin : plugins) {
                if ( incoming == null ) {
                    break;
                }
                incoming = plugin.setterBuilt(declaration, incoming, eventType);
            }

            return incoming;
        }
    }
}
