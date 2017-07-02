package org.raml.parsertools;

import org.junit.Assert;
import org.junit.Test;
import org.raml.testutils.UnitTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public class AugmenterTest  extends UnitTest {

    public interface Foo {

        String getName();
    }

    @Extension(handler = BooHandler.class)
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

    @ExtensionFactory(factory = Factory.class)
    public interface AugmentedNode  {

        int visit();
    }

    public interface SubFoo extends Foo {

        int subbing();
    }

    public static class SubFooHandler implements AugmentedNode {


        private SubFoo delegate;

        public SubFooHandler(SubFoo delegate) {

            this.delegate = delegate;
        }

        @Override
        public int visit() {
            return 0;
        }
    }

    public static class Factory implements AugmentationExtensionFactory {

        @Override
        public Object create(Object object) {

            throw new RuntimeException("argh");
        }

        public Object create(SubFoo delegate) {

            return new SubFooHandler(delegate);
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

        Assert.assertEquals("HandledBibi", b.getBibi());
        Assert.assertEquals("getName", b.getName());
        Assert.assertEquals("toString", b.toString());
    }

    @Test
    public void factory() throws Exception {

        SubFoo subFoo = (SubFoo) Proxy.newProxyInstance(AugmenterTest.class.getClassLoader(), new Class[] {SubFoo.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                return method.getName();
            }
        });
        AugmentedNode b = Augmenter.augment(AugmentedNode.class, subFoo);

        Assert.assertEquals("toString", b.toString());

    }
}
