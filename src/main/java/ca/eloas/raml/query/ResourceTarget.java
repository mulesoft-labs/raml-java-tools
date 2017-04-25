package ca.eloas.raml.query;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 4/22/17.
 * Just potential zeroes and ones
 */
public class ResourceTarget implements TargetType<Resource> {

    @Override
    public Iterable<Resource> get() {
        return null;
    }

    @Override
    public SelectionTarget<Resource> fromApi(Api api) {
        return new SelectionTarget<>(api.resources());
    }

    @Override
    public SelectionTarget<Resource> fromResource(Resource resource) {
        return null;
    }
}
