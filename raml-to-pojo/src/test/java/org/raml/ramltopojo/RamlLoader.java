package org.raml.ramltopojo;

import amf.client.model.domain.DomainElement;
import amf.client.model.domain.Shape;
import amf.client.validate.ValidationReport;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created. There, you have it.
 */
public class RamlLoader {

    public static WebApiDocument load(URL url) throws ExecutionException, InterruptedException {

        WebApiDocument document = (WebApiDocument) Raml10.parse(url.toString()).get();

        ValidationReport report = Raml10.validate(document).get();
        List<amf.client.validate.ValidationResult> results = report.results();
        if ( results.isEmpty()) {

            return document;
        } else {
            results.forEach(r -> System.err.println(r.message()));
            throw new IllegalArgumentException();
        }
    }

    public static <T extends Shape> T findShape(final String name, List<DomainElement> types) {
        return types.stream().filter(x -> x instanceof Shape).map(x -> (T)x).filter(input -> input.name().value().equals(name)).findFirst().get();
    }

}
