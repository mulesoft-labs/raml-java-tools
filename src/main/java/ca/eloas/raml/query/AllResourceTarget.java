package ca.eloas.raml.query;

import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import static com.google.common.collect.FluentIterable.from;

/**
 * Created by Jean-Philippe Belanger on 4/28/17.
 * Just potential zeroes and ones
 */
public class AllResourceTarget implements Target<Resource> {

    @Override
    public FluentIterable<Resource> fromApi(Api api) {

        FluentIterable<Resource> fi = from(api.resources());
        for (Resource resource : api.resources()) {
            fi = fi.append(fromResource(resource));
        }

        return fi;
    }

    @Override
    public FluentIterable<Resource> fromResource(Resource topResource) {
        FluentIterable<Resource> fi = from(topResource.resources());
        for (Resource resource : topResource.resources()) {
            fi = fi.append(fromResource(resource));
        }

        return fi;
    }
}
