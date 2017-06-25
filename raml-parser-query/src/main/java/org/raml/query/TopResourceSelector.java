package org.raml.query;

import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import static com.google.common.collect.FluentIterable.from;

/**
 * Created by Jean-Philippe Belanger on 4/22/17.
 * Just potential zeroes and ones
 */
public class TopResourceSelector implements Selector<Resource> {

    @Override
    public FluentIterable<Resource> fromApi(Api api) {
        return from(api.resources());
    }

    @Override
    public FluentIterable<Resource> fromResource(Resource resource) {
        return FluentIterable.from(resource.resources());
    }
}
