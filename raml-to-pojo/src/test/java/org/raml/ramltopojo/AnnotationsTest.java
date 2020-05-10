package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.Shape;
import amf.client.validate.ValidationReport;
import amf.client.validate.ValidationResult;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.raml.testutils.UnitTest;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class AnnotationsTest extends UnitTest{


    @Test
    public void apiAnnotationsReading() throws Exception {

        Document api = getApi();

        List<PluginDef> defs = Annotations.PLUGINS.get(api);
        assertEquals(2, defs.size());
        assertEquals("core.one", defs.get(0).getPluginName());
        assertEquals(Arrays.asList("foo", "bar"), defs.get(0).getArguments());
        assertEquals("core.two", defs.get(1).getPluginName());
        assertEquals(Arrays.asList("alpha", "gamma"), defs.get(1).getArguments());
    }

    @Test
    public void noAnnotations() throws Exception {

        Document api = getApi();
        Shape fooType = RamlLoader.findShape("none", api.declares());

        List<PluginDef> defs = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), fooType);
        assertEquals(0, defs.size());
    }

    @Test
    public void typeAnnotationsReading() throws Exception {

        Document api = getApi();
        Shape fooType = RamlLoader.findShape("foo", api.declares());

        List<PluginDef> defs = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, fooType);
        assertEquals(3, defs.size());
        assertEquals("core.one", defs.get(0).getPluginName());
        assertEquals(Arrays.asList("foo", "bar"), defs.get(0).getArguments());
        assertEquals("core.two", defs.get(1).getPluginName());
        assertEquals(Arrays.asList("alpha", "gamma"), defs.get(1).getArguments());
        assertEquals("core.foo", defs.get(2).getPluginName());
        assertEquals(Arrays.asList("foo", "bar"), defs.get(2).getArguments());
    }

    @Test
    public void mappedArguments() throws Exception {

        Document api = getApi();
        Shape fooType = RamlLoader.findShape("namedFoo", api.declares());

        List<PluginDef> defs = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), fooType);
        assertEquals(1, defs.size());
        assertEquals("core.foo", defs.get(0).getPluginName());
        assertEquals(ImmutableMap.of("first", "foo", "second", "bar"), defs.get(0).getNamedArguments());
    }

    @Test
    public void simplerTypeAnnotationsReading() throws Exception {

        Document api = getApi();
        Shape fooType = RamlLoader.findShape("too", api.declares());

        List<PluginDef> defs = Annotations.PLUGINS.get(fooType);
        assertEquals(2, defs.size());
        assertEquals("core.moo", defs.get(1).getPluginName());
        assertEquals("core.too", defs.get(0).getPluginName());
    }

    protected Document getApi() throws Exception  {
        URL url = this.getClass().getResource("annotations.raml");
        WebApiDocument document = (WebApiDocument) Raml10.parse(url.toString()).get();

        ValidationReport report = Raml10.validate(document).get();
        List<ValidationResult> results = report.results();
        if ( results.isEmpty()) {
            return document;
        } else {
            results.forEach(r -> System.err.println(r.message()));
            throw new IllegalArgumentException();
        }
    }

}