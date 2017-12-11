package org.raml.ramltopojo;

import org.raml.ramltopojo.extensions.PluginContext;

import java.util.List;

/**
 * Created. There, you have it.
 */
public class PluginContextImpl implements PluginContext {
    private final GenerationContext generationContext;

    public PluginContextImpl(GenerationContext generationContext) {
        this.generationContext = generationContext;
    }

    @Override
    public List<CreationResult> childClasses(String ramlTypeName) {
        return generationContext.childClasses(ramlTypeName);
    }
}
