package org.raml.query.internal;

import org.raml.parsertools.Extend;
import org.raml.query.QueryBase;
import org.raml.v2.api.model.v10.api.Api;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
@Extend(handler = ApiQueryBaseHandler.class)
public interface ApiQueryBase extends Api, QueryBase {
}
