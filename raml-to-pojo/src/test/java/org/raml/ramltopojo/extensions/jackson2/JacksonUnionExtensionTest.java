package org.raml.ramltopojo.extensions.jackson2;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.squareup.javapoet.ClassName;
import org.junit.Test;
import org.raml.ramltopojo.*;
import org.raml.v2.api.model.v10.api.Api;

import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.raml.testutils.matchers.AnnotationSpecMatchers.annotationType;
import static org.raml.testutils.matchers.AnnotationSpecMatchers.member;
import static org.raml.testutils.matchers.CodeBlockMatchers.codeBlockContents;
import static org.raml.testutils.matchers.MethodSpecMatchers.methodName;
import static org.raml.testutils.matchers.TypeSpecMatchers.*;

/**
 * Created. There, you have it.
 */
public class JacksonUnionExtensionTest {

    @Test
    public void complexInlineUnion() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-mix-type.raml"), ".");
        RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.jackson2"));
        CreationResult r = ramlToPojo.buildPojos().creationResults().stream().filter(x -> x.getJavaName(EventType.INTERFACE).simpleName().equals("Foo")).findFirst().get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().get().toString());

        assertNotNull(r);
        assertThat(
            r.internalType("prop").getInterface(),
            is(
                allOf(
                    name(is(equalTo("PropUnion"))),
                    annotations(
                        containsInAnyOrder(
                            allOf(
                                annotationType(equalTo(ClassName.get(JsonSerialize.class))),
                                member("using", contains(codeBlockContents(equalTo("PropUnion.Serializer.class"))))
                            ),
                            allOf(
                                annotationType(equalTo(ClassName.get(JsonDeserialize.class))),
                                member("using", contains(codeBlockContents(equalTo("PropUnion.Deserializer.class"))))
                            )
                        )
                    ),
                    innerTypes(
                        contains(
                            allOf(
                                name(is(equalTo("Serializer"))),
                                methods(contains(
                                    allOf(methodName(equalTo("<init>"))),
                                    allOf(methodName(equalTo("serialize")))
                                ))
                            ),
                            allOf(
                                name(is(equalTo("Deserializer"))),
                                methods(containsInAnyOrder(
                                    allOf(methodName(equalTo("<init>"))),
                                    allOf(methodName(equalTo("isValidObject"))),
                                    allOf(methodName(equalTo("getNullValue"))),
                                    allOf(methodName(equalTo("deserialize")))
                                ))
                            ),
                            allOf(name(is(equalTo("UnionType"))))
                        )
                    )
                )
            )
        );
    }

    @Test(expected = GenerationException.class)
    public void ambiguousUnion() throws Exception {
        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-ambiguous-type.raml"), ".");
        RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.jackson2"));
        ramlToPojo.buildPojos().creationResults();
    }
}