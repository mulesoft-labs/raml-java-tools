package org.raml.query.internal;

import org.raml.parsertools.Extension;
import org.raml.query.QueryBase;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
@Extension(handler = ResourceQueryBaseHandler.class)
public interface ResourceQueryBase extends Resource, QueryBase {
}
