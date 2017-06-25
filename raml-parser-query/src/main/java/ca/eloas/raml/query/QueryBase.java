package ca.eloas.raml.query;

import com.google.common.collect.FluentIterable;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
public interface QueryBase {

   <B> FluentIterable<B> queryFor(Selector<B> selector);
}
