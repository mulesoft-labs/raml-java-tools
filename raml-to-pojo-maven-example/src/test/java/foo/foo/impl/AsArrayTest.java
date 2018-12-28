package foo.foo.impl;

import foo.foo.Dates;
import foo.foo.DatesImpl;
import foo.foo.WithAnArray;
import foo.foo.WithAnArrayImpl;
import org.junit.Test;

/**
 * Created. There, you have it.
 */
public class AsArrayTest {

    @Test
    public void check() {

        WithAnArray w = new WithAnArrayImpl();
        w.setAnotherArray(new Dates[] {new DatesImpl()});
        w.setSomeArray(new Integer[] {1,2,3,4});
    }
}
