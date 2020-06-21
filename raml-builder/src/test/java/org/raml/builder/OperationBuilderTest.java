package org.raml.builder;

import amf.client.model.domain.*;
import amf.client.validate.ValidationReport;
import amf.core.AMF;
import org.junit.Test;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;
import static org.raml.builder.ResourceBuilder.resource;

/**
 * Created. There, you have it.
 */
public class OperationBuilderTest {


    @Test
    public void dammit() throws ExecutionException, InterruptedException {

        AMF.init();

        WebApiDocument doc = new WebApiDocument();

        WebApi api = new WebApi();
        //CreativeWork cw = new CreativeWork();
        api.withName("foo");
        doc.withEncodes(api);
        Request request = new Request();
        request.withPayloads(Collections.singletonList(new Payload().withMediaType("application/json").withSchema(new ScalarShape().withDataType("http://www.w3.org/2001/XMLSchema#string"))));
        EndPoint o = new EndPoint().withPath("/something");
        Operation get = new Operation().withMethod("get").withRequest(request);
        o.withOperations(Collections.singletonList(get));

        api.withEndPoints(Collections.singletonList(o));

        ValidationReport s = Raml10.validate(doc).get();
        System.err.println("Results:" + s);

        System.err.println(Raml10.generateString(doc).get());
    }

    @Test
    public void simpleMethod() throws ExecutionException, InterruptedException {

        WebApiDocument document = document()
                .baseUri("http://google.com")
                .title("doc")
                .version("one")
                .mediaType("foo/fun")
                .withResources(
                        resource("/foo")
                            .withMethods(OperationBuilder.method("get")
                                    .withQueryParameter(ParameterBuilder.queryParameter("foo").ofType(TypeShapeBuilder.stringScalar()))
                                    .withHeaderParameters(ParameterBuilder.headerParameter("faf").ofType(TypeShapeBuilder.stringScalar()))
                                    .withPayloads(PayloadBuilder.body("application/json"))
                            )
                )
                .buildModel();

        System.err.println(Raml10.generateString(document).get());

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