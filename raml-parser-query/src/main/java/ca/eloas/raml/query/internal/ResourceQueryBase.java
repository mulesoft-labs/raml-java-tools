package ca.eloas.raml.query.internal;

import ca.eloas.raml.query.Extend;
import ca.eloas.raml.query.QueryBase;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
@Extend(handler = ResourceQueryBaseHandler.class)
public interface ResourceQueryBase extends Resource, QueryBase {
}
