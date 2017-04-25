package ca.eloas.raml.query;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.resources.ResourceBase;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public interface TargetType<T> {

    Iterable<T> get();
    SelectionTarget<T> fromApi(Api api);
    SelectionTarget<T> fromResource(Resource resource);
}
