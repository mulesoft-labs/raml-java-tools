package org.raml.query;

import com.google.common.collect.FluentIterable;
import org.raml.parsertools.Augmenter;
import org.raml.query.internal.ApiQueryBase;
import org.raml.query.internal.ResourceQueryBase;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 4/19/17.
 * Just potential zeroes and ones
 */
public class Query {

    private QueryBase queryBase;

    public Query(QueryBase queryBase) {

        this.queryBase = queryBase;
    }

    /*
     *  Starting points
     */
    public static Query from(Api api) {

        return new Query(Augmenter.augment(ApiQueryBase.class, api));
    }

    public static Query from(Resource resource) {

        return new Query(Augmenter.augment(ResourceQueryBase.class, resource));
    }


    public<T> FluentIterable<T> select(Selector<T> selector) {

        return queryBase.queryFor(selector);
    }
}
