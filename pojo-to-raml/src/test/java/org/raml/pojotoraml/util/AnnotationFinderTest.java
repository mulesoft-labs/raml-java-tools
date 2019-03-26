package org.raml.pojotoraml.util;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.pojotoraml.plugins.RamlGenerators;

import static org.raml.pojotoraml.util.AnnotationFinder.annotationFor;

public class AnnotationFinderTest {
    
    private static final String PKG_NAME = "org.raml.pojotoraml.util";

    private static final Package TOP_PACKAGE = Package.getPackage(PKG_NAME);

    @BeforeClass
    public static void setUpOnce() throws Exception {

        assertNotNull("Top Package — null not expected", TOP_PACKAGE);
    }

    @Test
    public void testAnnotationForPackageShouldAcceptWildCardClass() throws Exception {

        Class<? extends RamlGenerators> input = RamlGenerators.class;

        RamlGenerators generators = annotationFor(TOP_PACKAGE, input);

        assertNotNull("Wildcard — null return not expected", generators);
    }

    @Test
    public void testAnnotationForPackageShouldAcceptClassLiteral() throws Exception {

        RamlGenerators generators = annotationFor(TOP_PACKAGE,
                RamlGenerators.class);
        assertNotNull("Class literal — null return not expected", generators);
    }

    @Test
    public void testAnnotationForPackageShouldAcceptTypeWitness() throws Exception {

        RamlGenerators generators = AnnotationFinder.<RamlGenerators>annotationFor(TOP_PACKAGE,
                RamlGenerators.class);
        assertNotNull("Type witness — null return not expected", generators);
    }
}
