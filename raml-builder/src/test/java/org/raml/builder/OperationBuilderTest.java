package org.raml.builder;

import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;

import static org.junit.Assert.assertEquals;
import static org.raml.builder.RamlDocumentBuilder.document;
import static org.raml.builder.ResourceBuilder.resource;

/**
 * Created. There, you have it.
 */
public class OperationBuilderTest {


    @Test
    public void simpleMethod() {

        Api api = document()
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

        assertEquals("get", api.resources().get(0).methods().get(0).method());
        assertEquals("foo", api.resources().get(0).methods().get(0).queryParameters().get(0).name());
        assertEquals("string", api.resources().get(0).methods().get(0).queryParameters().get(0).type());
        assertEquals("faf", api.resources().get(0).methods().get(0).headers().get(0).name());
        assertEquals("string", api.resources().get(0).methods().get(0).headers().get(0).type());
        assertEquals("application/json", api.resources().get(0).methods().get(0).body().get(0).name());
        assertEquals("any", api.resources().get(0).methods().get(0).body().get(0).type());

    }
}