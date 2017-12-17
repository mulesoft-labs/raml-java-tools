package org.raml.ramltopojo;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.ramltopojo.extensions.PluginContext;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class PluginContextImpl implements PluginContext {
    private final GenerationContext generationContext;
    private final CreationResult result;

    public PluginContextImpl(GenerationContext generationContext, CreationResult result) {
        this.generationContext = generationContext;
        this.result = result;
    }

    @Override
    public Set<CreationResult> childClasses(String ramlTypeName) {

        return FluentIterable.from(generationContext.childClasses(ramlTypeName)).transform(new Function<String, CreationResult>() {
            @Nullable
            @Override
            public CreationResult apply(@Nullable String input) {
                return generationContext.findCreatedType(input, null);
            }
        }).toSet();
    }

    @Override
    public CreationResult creationResult() {

        return result;
    }
}
