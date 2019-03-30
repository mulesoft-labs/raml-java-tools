package org.raml.pojotoraml.util;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationFinder {

    private static final Logger logger = LoggerFactory.getLogger("AnnotationFinder");

    public static <T extends Annotation> T annotationFor(Package aPkg, Class< ? extends  T> whichAnnotation) {

        T annotation = null;

        /* First, try the most straight-forward (pre-JDK 9) way to get it... */
        if ((annotation = aPkg.getAnnotation(whichAnnotation)) == null) {
            /* If we didn't get it on that first attempt, try a little harder... */
            annotation = find(aPkg, whichAnnotation);
        }
        return annotation;
    }

    private static <T extends Annotation> T find(Package aPkg, Class<? extends T> which) {

        T found = null;
          try {
            /*
             * Even though the package might be in our classpath, it might have been
             * loaded in a different class loader before getting here. It might,
             * therefore, not be available in ours. Explicity load it in ours...
             */
            String pkgName = aPkg.getName() + ".package-info";
            Class<?> klazz = AnnotationFinder.class.getClassLoader().loadClass(pkgName);
            logger.debug("{} Attempted a reload of class '{}'\n", "******* ", pkgName, " *******");
            /* Now try it... */
            found = aPkg.getAnnotation(which);
            logger.debug("{} After class reload attempt, T: '{} ({})' {}\n", "******* ",
                    found, klazz.getDeclaredAnnotations(), " *******");
            /* Depending on which JDK version we're in, it still might not be where we expect.
               So try one more thing... */
            Annotation[] annotations = {};
            if (found == null && (annotations = klazz.getDeclaredAnnotations()) != null) {
                found = (T) annotations[0];
            }
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        return found;
    }
}
