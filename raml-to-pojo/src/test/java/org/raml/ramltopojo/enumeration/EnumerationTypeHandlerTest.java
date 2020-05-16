package org.raml.ramltopojo.enumeration;

import amf.client.model.document.Document;
import amf.client.model.domain.ScalarShape;
import com.squareup.javapoet.ClassName;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.FilterableTypeFinder;
import org.raml.ramltopojo.GenerationContextImpl;
import org.raml.ramltopojo.RamlLoader;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.testutils.UnitTest;
import org.raml.testutils.matchers.FieldSpecMatchers;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.raml.ramltopojo.RamlLoader.findShape;
import static org.raml.testutils.matchers.TypeSpecMatchers.fields;
import static org.raml.testutils.matchers.TypeSpecMatchers.name;

/**
 * Created. There, you have it.
 */
public class EnumerationTypeHandlerTest extends UnitTest {


    @Test
    public void createString() throws Exception {

        Document api = RamlLoader.load(this.getClass().getResource("stringenum.raml"));
        ScalarShape enumShape = findShape("days", api.declares());
        EnumerationTypeHandler handler = new EnumerationTypeHandler("days", enumShape);
        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.NULL, api, new FilterableTypeFinder(), (x) -> true, (x, y) -> {}, "bar.pack", Collections.<String>emptyList());
        generationContext.newExpectedType("Days", new CreationResult(enumShape, "bar.pack", ClassName.get("bar.pack", "Days"), null));

        CreationResult result = handler.create(generationContext, new CreationResult(enumShape, "bar.pack", ClassName.get("bar.pack", "Days"), null)).get();

        assertThat(result.getInterface(), allOf(
                name(equalTo("Days")),
                fields(Matchers.contains(
                        FieldSpecMatchers.fieldName(equalTo("name"))
                        )
                )
        ));

        System.err.println(result.getInterface().toString());
    }

    @Test
    public void createInteger() throws Exception {

        Document api = RamlLoader.load(this.getClass().getResource("stringenum.raml"));
        ScalarShape enumShape = findShape("time", api.declares());
        EnumerationTypeHandler handler = new EnumerationTypeHandler("time", enumShape);
        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.NULL, api, new FilterableTypeFinder(), (x) -> true, (x,y) -> {}, "bar.pack", Collections.<String>emptyList());
        generationContext.newExpectedType("Time", new CreationResult(enumShape, "bar.pack", ClassName.get("bar.pack", "Time"), null));

        CreationResult result = handler.create(generationContext, new CreationResult(enumShape, "bar.pack", ClassName.get("bar.pack", "Time"), null)).get();

        assertThat(result.getInterface(), allOf(
                name(equalTo("Time")),
                fields(Matchers.contains(
                        FieldSpecMatchers.fieldName(equalTo("name"))
                        )
                )
        ));

        System.err.println(result.getInterface().toString());
    }

}