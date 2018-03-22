package org.raml.builder;

import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;

/**
 * Created. There, you have it.
 */
public class RamlDocumentBuilderTest {

    @Test
    public void emptyDocument() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .buildModel();

        assertEquals("http://google.com", api.baseUri().value());
        assertEquals("one", api.version().value());
        assertEquals("doc", api.title().value());
        assertEquals("foo/fun", api.mediaType().get(0).value());
    }

    @Test(expected = ModelBuilderException.class)
    public void missingTitle() {

        Api api = document()
                .baseUri("http://google.com")
                .version("one")
                .mediaType("foo/fun")
                .buildModel();

        assertEquals("http://google.com", api.baseUri().value());
        assertEquals("one", api.version().value());
        assertEquals("doc", api.title().value());
        assertEquals("foo/fun", api.mediaType().get(0).value());
    }

}