package ca.eloas.raml.query;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public class AugmenterTest {

    public interface Foo {

        String getName();
    }

    @Extend(handler = BooHandler.class)
    public interface Boo extends Foo {

        String getBibi();
    }

    public static class BooHandler {

        private final Foo foo;

        public BooHandler(Foo foo) {

            this.foo = foo;
        }

        public String getBibi() {

            return "HandledBibi";
        }
    }

    @Test
    public void simple() throws Exception {

        Foo foo = (Foo) Proxy.newProxyInstance(AugmenterTest.class.getClassLoader(), new Class[] {Foo.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                return method.getName();
            }
        });
        Boo b = Augmenter.augment(Boo.class, foo);

        assertEquals("HandledBibi", b.getBibi());
        assertEquals("getName", b.getName());
        assertEquals("toString", b.toString());


    }
}
