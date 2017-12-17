package org.raml.ramltopojo;

import org.junit.Test;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class AnnotationsTest extends UnitTest{


    @Test
    public void apiAnnotationsReading() throws IOException {

        Api api = getApi();

        List<PluginDef> defs = Annotations.PLUGINS.get(api);
        assertEquals(2, defs.size());
        assertEquals("core.one", defs.get(0).getPluginName());
        assertEquals(Arrays.asList("foo", "bar"), defs.get(0).getArguments());
        assertEquals("core.two", defs.get(1).getPluginName());
        assertEquals(Arrays.asList("alpha", "gamma"), defs.get(1).getArguments());
    }

    @Test
    public void typeAnnotationsReading() throws IOException {

        Api api = getApi();
        TypeDeclaration fooType = RamlLoader.findTypes("foo", api.types());

        List<PluginDef> defs = Annotations.PLUGINS.get(fooType);
        assertEquals(1, defs.size());
        assertEquals("core.foo", defs.get(0).getPluginName());
        assertEquals(Arrays.asList("foo", "bar"), defs.get(0).getArguments());
    }

    @Test
    public void simplerTypeAnnotationsReading() throws IOException {

        Api api = getApi();
        TypeDeclaration fooType = RamlLoader.findTypes("too", api.types());

        List<PluginDef> defs = Annotations.PLUGINS.get(fooType);
        assertEquals(2, defs.size());
        assertEquals("core.too", defs.get(0).getPluginName());
        assertEquals("core.moo", defs.get(1).getPluginName());
    }

    protected Api getApi() throws IOException {
        URL url = this.getClass().getResource("annotations.raml");
        return RamlLoader.load(url.openStream(), new File(url.getFile()).getAbsolutePath());
    }

}