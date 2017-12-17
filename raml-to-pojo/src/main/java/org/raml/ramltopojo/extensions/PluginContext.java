package org.raml.ramltopojo.extensions;

import org.raml.ramltopojo.CreationResult;

import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface PluginContext {

    Set<CreationResult> childClasses(String ramlTypeName);

    CreationResult creationResult();
}
