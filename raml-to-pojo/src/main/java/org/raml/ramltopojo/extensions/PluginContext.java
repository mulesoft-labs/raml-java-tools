package org.raml.ramltopojo.extensions;

import org.raml.ramltopojo.CreationResult;

import java.util.List;

/**
 * Created. There, you have it.
 */
public interface PluginContext {

    List<CreationResult> childClasses(String ramlTypeName);
}
