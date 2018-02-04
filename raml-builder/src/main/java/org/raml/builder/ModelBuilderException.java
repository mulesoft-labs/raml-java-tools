package org.raml.builder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import org.raml.yagi.framework.nodes.ErrorNode;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ModelBuilderException extends RuntimeException {

    private final List<ErrorNode> errors;

    public ModelBuilderException(List<ErrorNode> errors) {

        this.errors = errors;
    }

    @Override
    public String getMessage() {

        return "model errors: " + Joiner.on(",").join(FluentIterable.from(errors).transform(new Function<ErrorNode, String>() {
            @Nullable
            @Override
            public String apply(@Nullable ErrorNode errorNode) {
                return errorNode.getErrorMessage();
            }
        }));
    }
}
