package ca.eloas.raml.query;

import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.List;

import static com.google.common.collect.FluentIterable.from;

/**
 * Created by Jean-Philippe Belanger on 4/28/17.
 * Just potential zeroes and ones
 */
public class AllResourceSelector implements Selector<Resource> {

    @Override
    public FluentIterable<Resource> fromApi(Api api) {

        List<Resource> topResources = api.resources();
        FluentIterable<Resource> fi = from(topResources);
        for (Resource resource : topResources) {
            fi = fi.append(fromResource(resource));
        }

        return fi;
    }

    @Override
    public FluentIterable<Resource> fromResource(Resource topResource) {
        List<Resource> resources = topResource.resources();
        FluentIterable<Resource> fi = from(resources);
        for (Resource resource : resources) {
            fi = fi.append(fromResource(resource));
        }

        return fi;
    }
}
