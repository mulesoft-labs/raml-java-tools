package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.raml.ramltopojo.extensions.ReferencePluginContext;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class AsArray implements ReferenceTypeHandlerPlugin {

/*
    private final List<String> arguments;

    public AsArray(List<String> arguments) {
        this.arguments = arguments;
    }
*/

    @Override
    public TypeName typeName(ReferencePluginContext referencePluginContext, TypeDeclaration ramlType, TypeName currentSuggestion) {

        if ( ramlType instanceof ArrayTypeDeclaration) {
            return ArrayTypeName.of(((ParameterizedTypeName)currentSuggestion).typeArguments.get(0).box());
        } else {

            return currentSuggestion;
        }
    }
}
