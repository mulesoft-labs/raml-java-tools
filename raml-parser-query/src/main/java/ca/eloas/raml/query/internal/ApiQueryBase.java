package ca.eloas.raml.query.internal;

import ca.eloas.raml.query.Extend;
import ca.eloas.raml.query.QueryBase;
import org.raml.v2.api.model.v10.api.Api;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
@Extend(handler = ApiQueryBaseHandler.class)
public interface ApiQueryBase extends Api, QueryBase {
}
