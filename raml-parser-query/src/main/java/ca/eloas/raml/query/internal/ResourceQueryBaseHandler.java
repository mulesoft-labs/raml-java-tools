package ca.eloas.raml.query.internal;

import ca.eloas.raml.query.QueryBase;
import ca.eloas.raml.query.Selector;
import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
public class ResourceQueryBaseHandler implements QueryBase {

    private final Resource resource;

    public ResourceQueryBaseHandler(Resource resource) {

        this.resource = resource;
    }

    @Override
    public <B> FluentIterable<B> queryFor(Selector<B> selector) {
        return selector.fromResource(resource);
    }

}
