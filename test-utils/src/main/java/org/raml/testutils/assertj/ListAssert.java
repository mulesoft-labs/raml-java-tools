package org.raml.testutils.assertj;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created. There, you have it.
 */
public class ListAssert {

    public static<T> void listMatches(List<? extends T> info, List<Consumer<T>> assertions) {

        if ( info.size() != assertions.size()) {

            throw new AssertionError("lists are not the same length");
        }

        Iterator<? extends T> infoIterator = info.iterator();
        assertions.forEach(c -> c.accept(infoIterator.next()));
    }

    public static<T> void listMatches(List<? extends T> info, Consumer<T>... assertions) {

        listMatches(info, Arrays.asList(assertions));
    }

}
