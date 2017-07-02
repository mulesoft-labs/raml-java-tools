package org.raml.parsertools;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
public class Augmenter {

    private static class SingleClassFactory implements AugmentationExtensionFactory {

        private final Class<?> handlerClass;

        public SingleClassFactory(Class<?> cls) {
            this.handlerClass = cls;
        }

        public Object create(Object delegate) throws AugmentationException {

            try {
                Constructor<?> c = handlerClass.getConstructor(delegate.getClass().getInterfaces()[0]);
                final Object handler = c.newInstance(delegate);
                return handler;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new AugmentationException(e);
            }
        }
    }

    public static<T> T augment(Class<T> augmentedInterface, final Object delegate) {

        try {
            Extension extension = augmentedInterface.getAnnotation(Extension.class);
            ExtensionFactory extensionFactory = augmentedInterface.getAnnotation(ExtensionFactory.class);

            if ( extension == null && extensionFactory == null ) {
                throw new IllegalArgumentException("no @Extension or @ExtensionFactory annotation to build augmented interface");
            }

            AugmentationExtensionFactory factory = createFactory(extension, extensionFactory);
            final Object handler = findFactoryMethod(delegate, factory);

            return buildProxy(augmentedInterface, delegate, handler);
        } catch (NoSuchMethodException| IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new AugmentationException("trying to augment " + augmentedInterface, e);
        }
    }

    private static <T> T buildProxy(Class<T> augmentedInterface, final Object delegate, final Object handler) {
        return (T) Proxy.newProxyInstance(Augmenter.class.getClassLoader(), new Class[] {augmentedInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        try {
                            Method handlerMethod = handler.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                            return handlerMethod.invoke(handler, args);
                        } catch (NoSuchMethodException e) {
                            return method.invoke(delegate, args);
                        }

                    }
                });
    }

    private static AugmentationExtensionFactory createFactory(Extension extension, ExtensionFactory extensionFactory) throws InstantiationException, IllegalAccessException {
        AugmentationExtensionFactory factory;
        if ( extensionFactory == null ) {

            factory = new SingleClassFactory(extension.handler());
        } else {

            factory = extensionFactory.factory().newInstance();
        }
        return factory;
    }

    private static Set<Class<?>> findAllImplementedInterfaces(Class<?> bottomClass) {

        Set<Class<?>> classes = new LinkedHashSet<>(Arrays.asList(bottomClass.getInterfaces()));

        Set<Class<?>> newClasses = new HashSet<>();
        for (Class<?> aClass : classes) {

            Set<Class<?>> higherClasses = findAllImplementedInterfaces(aClass);
            newClasses.addAll(higherClasses);
        }

        if ( bottomClass.getSuperclass() != null ) {
            newClasses.addAll(findAllImplementedInterfaces(bottomClass.getSuperclass()));
        }
        classes.addAll(newClasses);
        return classes;
    }

    private static Object findFactoryMethod(Object delegate, AugmentationExtensionFactory factory) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Set<Class<?>> classes = findAllImplementedInterfaces(delegate.getClass());
        for (Class<?> aClass : classes) {

            try {
                return factory.getClass().getDeclaredMethod("create", aClass).invoke(factory, delegate);
            } catch (NoSuchMethodException e) {
            }
        }

        return factory.create(delegate);

    }
}
