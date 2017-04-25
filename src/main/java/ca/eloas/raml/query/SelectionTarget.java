package ca.eloas.raml.query;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public class SelectionTarget<T> {

    private final Iterable<T> target;

    public SelectionTarget(Iterable<T> target) {

        this.target = target;
    }

/*
    public SelectionTarget<R> like(Matcher<? super R> matcher) {

        return this;
    }
*/
}
