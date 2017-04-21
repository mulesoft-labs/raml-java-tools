package ca.eloas.raml.query;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Iterator;

import static org.hamcrest.core.IsInstanceOf.any;

/**
 * Created by Jean-Philippe Belanger on 4/19/17.
 * Just potential zeroes and ones
 */
public class Query {

    public static Query from(Api api) {

        return new Query(/*api*/);
    }

    public SelectionTarget selectAll(TypeTarget target) {

        return null;
    }

    public static void main(String[] args) {

        Api api = null;

        from(api).selectAll(resources()).like(any(String.class));
    }

    private static TypeTarget resources() {

        return null;
    }


}
