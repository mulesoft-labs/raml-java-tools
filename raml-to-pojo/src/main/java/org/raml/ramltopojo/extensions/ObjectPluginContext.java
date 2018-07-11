package org.raml.ramltopojo.extensions;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.CreationResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ObjectPluginContext {

    Set<CreationResult> childClasses(String ramlTypeName);
    CreationResult creationResult();
    CreationResult dependentType(TypeDeclaration items);
    TypeName forProperty(TypeDeclaration typeDeclaration);

    TypeName createSupportClass(TypeSpec.Builder newSupportType);
}
