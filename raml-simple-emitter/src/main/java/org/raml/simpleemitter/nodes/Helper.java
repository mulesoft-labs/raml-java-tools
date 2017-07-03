package org.raml.simpleemitter.nodes;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.api.ModifiableResource;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class Helper {

    protected <E, M extends E> List<E> toModifiable(List<E> list, final Class<M> cls) {
        List<M> ms =  FluentIterable.from(list).transform(new Function<E, M>() {
            @Nullable
            @Override
            public M apply(@Nullable E resource) {

                return Augmenter.augment(cls, resource);
            }
        }).toList();

        return new ArrayList<E>(ms);
    }
}
