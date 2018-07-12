package org.raml.ramltopojo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created. There, you have it.
 */
public class EcmaPattern {

    static private final Pattern ECMA_WITH_SLASHES = Pattern.compile("^/(.*)/([a-z]*)$");
    private final Pattern pattern;

    public EcmaPattern(Pattern pattern) {

        this.pattern = pattern;
    }

    public static boolean isSlashedPattern(String name) {
        return ECMA_WITH_SLASHES.matcher(name).matches();
    }

    public String asJavaPattern() {

        return pattern.toString();
    }

    public static EcmaPattern fromString(String ecmaPattern) {


        Matcher matcher = ECMA_WITH_SLASHES.matcher(ecmaPattern);
        if ( matcher.matches()) {

            return ecmaToJavaRegexp(matcher.group(1), matcher.group(2));
        } else {

            return ecmaToJavaRegexp(ecmaPattern, "");
        }
    }

    private static EcmaPattern ecmaToJavaRegexp(String ecmaPattern, String options) {
        int flags = 0;

        if ( options.contains("i") ) {
            flags |= Pattern.CASE_INSENSITIVE;
        }

        if ( options.contains("m") ) {
            flags |= Pattern.MULTILINE;
        }

        return new EcmaPattern(Pattern.compile(ecmaPattern, flags));
    }

    public int flags() {
        return pattern.flags();
    }
}
