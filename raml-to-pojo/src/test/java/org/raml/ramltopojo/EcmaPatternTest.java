package org.raml.ramltopojo;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class EcmaPatternTest {

    @Test
    public void simple() {

        EcmaPattern pattern = EcmaPattern.fromString("/fun/");
        assertEquals("fun", pattern.asJavaPattern());
    }

    @Test
    public void simpleWithSomeFlags() {

        EcmaPattern pattern = EcmaPattern.fromString("/fun/i");
        assertEquals("fun", pattern.asJavaPattern());
        assertEquals(Pattern.CASE_INSENSITIVE, pattern.flags());

        pattern = EcmaPattern.fromString("/fun/m");
        assertEquals("fun", pattern.asJavaPattern());
        assertEquals(Pattern.MULTILINE, pattern.flags());

        pattern = EcmaPattern.fromString("/fun/im");
        assertEquals("fun", pattern.asJavaPattern());
        assertEquals(Pattern.CASE_INSENSITIVE|Pattern.MULTILINE, pattern.flags());

    }

    @Test
    public void extraBackslashing() {

        EcmaPattern pattern = EcmaPattern.fromString("/fun\\[value\\]/");
        assertEquals("fun\\[value\\]", pattern.asJavaPattern());
    }

    @Test
    public void simpleUnslashed() {

        EcmaPattern pattern = EcmaPattern.fromString("fun");
        assertEquals("fun", pattern.asJavaPattern());
    }

}