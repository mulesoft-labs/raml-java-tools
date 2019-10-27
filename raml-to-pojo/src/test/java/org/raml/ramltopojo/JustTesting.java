package org.raml.ramltopojo;

import amf.client.model.StrField;
import amf.client.model.domain.DomainElement;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.WebApi;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created. There, you have it.
 */
public class JustTesting {

    // Example of parsing RAML 1.0 file
    public static void main2(String[] args) throws InterruptedException, ExecutionException {
        // Parse the file
        final WebApiDocument result = (WebApiDocument) Raml10.parse(JustTesting.class.getResource("/org/raml/ramltopojo/object/inherited-type.raml").toString()).get();

        // Log parsed model API
        List<DomainElement> shapes = result.declares();
        WebApi api = (WebApi) result.encodes();
        NodeShape nodeShape = ((NodeShape)api.endPoints().get(0).operations().get(0).responses().get(0).payloads().get(0).schema());
        List<StrField> nodes = api.schemes();
        System.out.println("Parsed Raml10 file. Expected unit encoding webapi: " + api);

    }

    // Example of parsing RAML 1.0 file
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String inp ="#%RAML 1.0\n" +
                "\n" +
                "title: ACME Banking HTTP API\n" +
                "version: 1.0";

        // Parse the string
        WebApiDocument doc = (WebApiDocument) Raml10.parse(inp).get();

        // Get parsed model API instance
        WebApi api = (WebApi) doc.encodes();

        // Set API version
        api.withVersion("3.7");

        // Set API description
        api.withDescription("Very nice api");

        // Generate RAML 1.0 string from updated model and log it
        String output = Raml10.generateString(doc).get();
        System.out.println("Generated Raml10 string:\n" + output);
    }
}
