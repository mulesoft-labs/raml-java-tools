package org.raml.builder;

import amf.client.model.document.Document;
import amf.client.model.domain.WebApi;
import amf.client.render.Raml10Renderer;
import amf.core.AMF;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;
import static org.raml.builder.ResourceBuilder.resource;

/**
 * Created. There, you have it.
 */
public class OperationBuilderTest {


    @Test
    public void simpleMethod() throws ExecutionException, InterruptedException {

        AMF.init();
        Class c = WebApi.class;

        Document document = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withResources(
                        resource("/foo")
                            .withMethods(OperationBuilder.method("get")
                                    .withQueryParameter(ParameterBuilder.parameter("foo").ofType("string"))
                                    .withHeaderParameters(ParameterBuilder.parameter("faf").ofType("string"))
                                    .withPayloads(PayloadBuilder.body("application/json"))
                            )
                )
                .buildModel();

        Raml10Renderer rr = new Raml10Renderer();
        String s = rr.generateString(document).get();
        WebApi api = (WebApi) document.encodes();
        assertEquals("get", api.endPoints().get(0).operations().get(0).method().value());
        assertEquals("foo", api.endPoints().get(0).operations().get(0).request().queryParameters().get(0).name().value());
        assertEquals("string", api.endPoints().get(0).operations().get(0).request().queryParameters().get(0).schema().name().value());
        assertEquals("faf", api.endPoints().get(0).operations().get(0).request().headers().get(0).name().value());
        assertEquals("string", api.endPoints().get(0).operations().get(0).request().headers().get(0).schema().name().value());
        assertEquals("application/json", api.endPoints().get(0).operations().get(0).request().payloads().get(0).mediaType().value());
        assertEquals("any", api.endPoints().get(0).operations().get(0).request().payloads().get(0).schema().name().value());
    }
}