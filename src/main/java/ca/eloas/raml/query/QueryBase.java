package ca.eloas.raml.query;

/**
 * Created by Jean-Philippe Belanger on 4/21/17.
 * Just potential zeroes and ones
 */
public interface QueryBase {

   <B> SelectionTarget<B> queryFor(TargetType<B> target);
}
