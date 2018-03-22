package org.raml.builder;

import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;
import static org.raml.builder.ResourceBuilder.resource;

/**
 * Created. There, you have it.
 */
public class ResponseBuilderTest {

    @Test
    public void response() {

        Api api = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withResources(
                        resource("/foo")
                                .withMethods(MethodBuilder.method("get")
                                        .withResponses(ResponseBuilder.response(200)
                                                .withBodies(BodyBuilder.body("application/json").ofType(TypeBuilder.type("integer")))
                                        )
                                )
                )
                .buildModel();

        assertEquals("application/json", api.resources().get(0).methods().get(0).responses().get(0).body().get(0).name());
        assertEquals("integer", api.resources().get(0).methods().get(0).responses().get(0).body().get(0).type());

    }

}