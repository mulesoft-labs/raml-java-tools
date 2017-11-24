package org.raml.testutils.matchers;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created. There, you have it.
 */
public class TypeNameMatcher {

    public static Matcher<TypeName> typeName(final Matcher<? super ClassName> matcher) {

        final Matcher<? super ClassName> subMatcher = matcher;
        return new TypeSafeMatcher<TypeName>() {
            @Override
            protected boolean matchesSafely(TypeName item) {
                return subMatcher.matches(item);
            }

            @Override
            public void describeTo(Description description) {

                description.appendText("typename ").appendDescriptionOf(subMatcher);
            }
        };
    }
}
