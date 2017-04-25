package ca.eloas.raml.query;

import ca.eloas.raml.query.internal.ApiQueryBase;
import ca.eloas.raml.query.internal.ResourceQueryBase;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;

/**
 * Created by Jean-Philippe Belanger on 4/19/17.
 * Just potential zeroes and ones
 */
public class Query<B> {

    private QueryBase queryBase;

    public Query(QueryBase queryBase) {

        this.queryBase = queryBase;
    }

    /*
    Starting points
     */
    public static Query<Api> from(Api api) {

        return new Query<>(Augmenter.augment(ApiQueryBase.class, api));
    }

    public static Query<Resource> from(Resource resource) {

        return new Query<>(Augmenter.augment(ResourceQueryBase.class, resource));
    }


    public<T> SelectionTarget<T> selectAll(TargetType<T> target) {

        return queryBase.queryFor(target);
    }

    public static void main(String[] args) {

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(new File(Query.class.getResource("/api.raml").getFile()));
        if (ramlModelResult.hasErrors())
        {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults())
            {
                System.out.println(validationResult.getMessage());
            }

            return;
        }
        else {
            Api api = ramlModelResult.getApiV10();

            Query<Api> s = from(api);
            SelectionTarget<Resource> tr = s.selectAll(resources());
        }
    }

    private static TargetType<Resource> resources() {

        return new ResourceTarget();
    }


}
