package org.raml.ramltopojo.extensions;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.GenerationContext;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class ObjectPluginContextImpl implements ObjectPluginContext {
    private final GenerationContext generationContext;
    private final CreationResult result;

    public ObjectPluginContextImpl(GenerationContext generationContext, CreationResult result) {
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

    @Override
    public CreationResult dependentType(TypeDeclaration items) {
        return generationContext.findCreatedType(items.name(), items);
    }
}
