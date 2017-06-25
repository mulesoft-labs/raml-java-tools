package org.raml.parsertools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Jean-Philippe Belanger on 4/20/17.
 * Just potential zeroes and ones
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extend {
    Class<?> handler();
}
