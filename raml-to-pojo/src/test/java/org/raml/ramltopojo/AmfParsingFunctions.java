package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.Shape;
import amf.client.resolve.Raml10Resolver;
import amf.core.resolution.pipelines.ResolutionPipeline;
import webapi.Raml10;
import webapi.WebApiBaseUnit;

/**
 * Created. There, you have it.
 */
public class AmfParsingFunctions {
    public static String header() {
        return "#%RAML 1.0\n" +
                "title: Hello World API\n" +
                "version: v1\n" +
                "baseUri: https://api.github.com\n";
    }

    public  static <T extends Shape> T findDeclarationByName(Document doc) {
        return (T) doc.declares().stream()
                .filter(s -> s instanceof Shape)
                .map(s -> (T) s)
                .filter(s -> s.name().value().equals("mytype"))
                .findFirst().orElseThrow(() -> new RuntimeException("no such shape"));
    }

    public static  Document resolveDocument(String documentString) throws InterruptedException, java.util.concurrent.ExecutionException {

        WebApiBaseUnit data = Raml10.parse(header() + documentString).get();
        return (Document) new Raml10Resolver().resolve(data, ResolutionPipeline.EDITING_PIPELINE());
    }
}
