package foo.foo;

import org.junit.Test;

/**
 * Created. There, you have it.
 */
public class BananaTest {

    @Test
    public void testBananas() {

        // this is essentially a compile test....
        Banana b = new BananaImpl();
        SecondBanana b2 = new SecondBananaImpl();
        b.setAnotherBanana(b2);
        b.setYetAnotherBanana(new foo.foo.colors.BananaImpl());
    }
}
