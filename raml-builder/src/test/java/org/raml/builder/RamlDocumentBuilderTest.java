package org.raml.builder;

import amf.client.model.domain.WebApi;
import org.junit.Test;
import webapi.WebApiDocument;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;


/**
 * Created. There, you have it.
 */
public class RamlDocumentBuilderTest {

    @Test
    public void emptyDocument() throws ExecutionException, InterruptedException {

        WebApiDocument document = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .buildModel();



        WebApi api = (WebApi) document.encodes();
        assertEquals("one", api.version().value());
        assertEquals("doc", api.name().value());
        assertEquals("foo/fun", api.contentType().get(0).value());
       // assertEquals("http://google.com", api.servers().get(0).url().value());

    }

    //@Test(expected = ModelBuilderException.class)
    public void missingTitle() {

        WebApiDocument document = document()
                .baseUri("http://google.com")
                .version("one")
                .mediaType("foo/fun")
                .buildModel();
    }

}