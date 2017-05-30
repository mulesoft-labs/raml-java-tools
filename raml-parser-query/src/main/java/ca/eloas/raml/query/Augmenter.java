package ca.eloas.raml.query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public class Augmenter {

    public static<T> T augment(Class<T> augmentedInterface, final Object delegate) {

        try {
            Extend extend = augmentedInterface.getAnnotation(Extend.class);
            final Class<?> handlerClass = extend.handler();
            Constructor<?> c = handlerClass.getConstructor(delegate.getClass().getInterfaces()[0]);
            final Object handler = c.newInstance(delegate);

            return (T) Proxy.newProxyInstance(Augmenter.class.getClassLoader(), new Class[] {augmentedInterface},
                    new InvocationHandler() {
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                            try {
                                Method handlerMethod = handlerClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                                return handlerMethod.invoke(handler, args);
                            } catch (NoSuchMethodException e) {
                                return method.invoke(delegate, args);
                            }

                        }
                    });
        } catch (NoSuchMethodException| IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new AugmentationException("trying to augment " + augmentedInterface, e);
        }
    }
}
