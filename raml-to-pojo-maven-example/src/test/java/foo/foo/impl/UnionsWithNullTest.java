package foo.foo.impl;

import foo.foo.NilUnionType;
import foo.foo.NilUnionTypeImpl;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created. There, you have it.
 */
public class UnionsWithNullTest {

    @Test
    public void simple() {

        NilUnionType type = new NilUnionTypeImpl();

        assertTrue(type.isNil());
    }
}
