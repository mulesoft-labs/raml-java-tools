package org.raml.ramltopojo.extensions.jsr303;

import amf.client.model.document.Document;
import com.squareup.javapoet.ClassName;
import org.junit.Test;
import org.raml.ramltopojo.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.raml.testutils.matchers.AnnotationSpecMatchers.annotationType;
import static org.raml.testutils.matchers.AnnotationSpecMatchers.member;
import static org.raml.testutils.matchers.CodeBlockMatchers.codeBlockContents;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldAnnotations;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldName;
import static org.raml.testutils.matchers.TypeSpecMatchers.fields;
import static org.raml.testutils.matchers.TypeSpecMatchers.name;

public class Jsr303UnionExtensionTest {

    @Test
    public void unionValidation() throws Exception {

        Document api = RamlLoader.load(this.getClass().getResource("union-type.raml"));
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
