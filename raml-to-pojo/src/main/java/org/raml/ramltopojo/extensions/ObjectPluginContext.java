package org.raml.ramltopojo.extensions;

import org.raml.ramltopojo.CreationResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ObjectPluginContext extends GenericPluginContext {

    Set<CreationResult> childClasses(String ramlTypeName);
    CreationResult dependentType(TypeDeclaration items);
}
