package org.raml.builder;

import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;
import static org.raml.builder.ResourceBuilder.resource;

/**
 * Created. There, you have it.
 */
public class ResourceBuilderTest {

    @Test
    public void resourceBuilder() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withResources(
                        resource("/foo")
                                .description("happy")
                )
                .buildModel();

        assertEquals("/foo", api.resources().get(0).displayName().value());
        assertEquals("/foo", api.resources().get(0).resourcePath());
        assertEquals("/foo", api.resources().get(0).relativeUri().value());
        assertEquals("happy", api.resources().get(0).description().value());
    }

    @Test
    public void resourceBuilderDisplay() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withResources(
                        resource("/foo")
                                .displayName("displayed")
                )
                .buildModel();

        assertEquals("displayed", api.resources().get(0).displayName().value());
    }

    @Test
    public void subResourceBuilder() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withResources(
                        resource("/foo").withResources(resource("/goo"))
                )
                .buildModel();

        assertEquals("/goo", api.resources().get(0).resources().get(0).displayName().value());
        assertEquals("/foo/goo", api.resources().get(0).resources().get(0).resourcePath());
        assertEquals("/goo", api.resources().get(0).resources().get(0).relativeUri().value());
    }

}