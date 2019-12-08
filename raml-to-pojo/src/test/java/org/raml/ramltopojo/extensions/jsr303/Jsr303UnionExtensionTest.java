package org.raml.ramltopojo.extensions.jsr303;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.raml.testutils.matchers.AnnotationSpecMatchers.annotationType;
import static org.raml.testutils.matchers.AnnotationSpecMatchers.member;
import static org.raml.testutils.matchers.CodeBlockMatchers.codeBlockContents;
import static org.raml.testutils.matchers.TypeSpecMatchers.fields;
import static org.raml.testutils.matchers.TypeSpecMatchers.name;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldName;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldAnnotations;

import java.util.Arrays;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.junit.Test;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.RamlLoader;
import org.raml.ramltopojo.RamlToPojo;
import org.raml.ramltopojo.RamlToPojoBuilder;
import org.raml.ramltopojo.TypeFetchers;
import org.raml.ramltopojo.TypeFinders;
import org.raml.v2.api.model.v10.api.Api;

import com.squareup.javapoet.ClassName;

public class Jsr303UnionExtensionTest {

    @Test
    public void unionValidation() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-type.raml"), ".");
        RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.jsr303"));
        CreationResult r = ramlToPojo.buildPojos().creationResults().stream().filter(x -> x.getJavaName(EventType.INTERFACE).simpleName().equals("Foo")).findFirst().get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().get().toString());

        assertNotNull(r);
        assertThat(
            r.getImplementation().get(),
            is(
                allOf(
                    name(
                        is(equalTo("FooImpl"))
                    ),
                    fields(
                        contains(
                            allOf(fieldName(equalTo("unionType"))),
                            allOf(
                                fieldName(equalTo("emailValue")),
                                fieldAnnotations(
                                    allOf(
                                        containsInAnyOrder(
                                            allOf(
                                                annotationType(equalTo(ClassName.get(Size.class))),
                                                member("min", contains(codeBlockContents(equalTo("1"))))
                                            ),
                                            allOf(
                                                annotationType(equalTo(ClassName.get(Pattern.class))),
                                                member("regexp", contains(codeBlockContents(equalTo("\"^/.*$\""))))
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        );
    }
}
