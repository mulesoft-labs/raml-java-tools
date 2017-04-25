package ca.eloas.raml.query.internal;

import ca.eloas.raml.query.QueryBase;
import ca.eloas.raml.query.SelectionTarget;
import ca.eloas.raml.query.TargetType;
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
    public <B> SelectionTarget<B> queryFor(TargetType<B> target) {
        return target.fromResource(resource);
    }

}
