package ca.eloas.raml.query.internal;

import ca.eloas.raml.query.QueryBase;
import ca.eloas.raml.query.SelectionTarget;
import ca.eloas.raml.query.TargetType;
import org.raml.v2.api.model.v10.api.Api;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
public class ApiQueryBaseHandler implements QueryBase {

    private final Api api;

    public ApiQueryBaseHandler(Api api) {

        this.api = api;
    }

    @Override
    public <B> SelectionTarget<B> queryFor(TargetType<B> target) {
        return target.fromApi(api);
    }

}
