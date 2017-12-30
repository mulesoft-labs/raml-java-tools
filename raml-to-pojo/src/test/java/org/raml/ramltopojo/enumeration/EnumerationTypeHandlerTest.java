package org.raml.ramltopojo.enumeration;

import com.squareup.javapoet.ClassName;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.GenerationContextImpl;
import org.raml.ramltopojo.TypeFetchers;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.testutils.UnitTest;
import org.raml.testutils.matchers.FieldSpecMatchers;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.raml.testutils.matchers.TypeSpecMatchers.fields;
import static org.raml.testutils.matchers.TypeSpecMatchers.name;

/**
 * Created. There, you have it.
 */
public class EnumerationTypeHandlerTest extends UnitTest {

    @Mock
    StringTypeDeclaration declaration;

    @Test
    public void create() throws Exception {

        when(declaration.name()).thenReturn("Days");
        when(declaration.enumValues()).thenReturn(Arrays.asList("one", "two", "three"));

        EnumerationTypeHandler handler = new EnumerationTypeHandler("days", declaration);
        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.NULL, null, TypeFetchers.fromTypes(), "bar.pack");
        generationContext.newExpectedType("Days", new CreationResult("bar.pack", ClassName.get("bar.pack", "Days"), null));

        CreationResult result = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Days"), null));

        assertThat(result.getInterface(), allOf(
                name(equalTo("Days")),
                fields(Matchers.contains(
                        FieldSpecMatchers.fieldName(equalTo("name"))
                        )
                )
        ));

        System.err.println(result.getInterface().toString());
    }

}