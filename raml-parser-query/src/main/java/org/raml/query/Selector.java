package org.raml.query;

import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.resources.ResourceBase;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public interface Selector<T> {

    FluentIterable<T> fromApi(Api api);
    FluentIterable<T> fromResource(Resource resource);
}
