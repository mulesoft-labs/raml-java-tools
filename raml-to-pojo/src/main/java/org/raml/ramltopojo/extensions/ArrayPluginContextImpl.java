package org.raml.ramltopojo.extensions;

import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.GenerationContext;


/**
 * Created. There, you have it.
 */
public class ArrayPluginContextImpl implements ArrayPluginContext {
    private final GenerationContext generationContext;
    private final CreationResult result;

    public ArrayPluginContextImpl(GenerationContext generationContext, CreationResult result) {
        this.generationContext = generationContext;
        this.result = result;
    }

    @Override
    public CreationResult creationResult() {

        return result;
    }
}
