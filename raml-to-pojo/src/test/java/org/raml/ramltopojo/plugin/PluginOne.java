package org.raml.ramltopojo.plugin;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.object.ObjectTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class PluginOne implements ObjectTypeHandlerPlugin {

    @Override
    public TypeSpec.Builder classCreated(ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {
        return null;
    }

    @Override
    public FieldSpec.Builder fieldBuilt(TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType) {
        return null;
    }

    @Override
    public MethodSpec.Builder getterBuilt(TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
        return null;
    }

    @Override
    public MethodSpec.Builder setterBuilt(TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
        return null;
    }
}
