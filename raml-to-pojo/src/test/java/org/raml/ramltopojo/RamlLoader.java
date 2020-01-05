package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.DomainElement;
import amf.client.model.domain.Shape;
import amf.client.resolve.Raml10Resolver;
import amf.client.validate.ValidationReport;
import amf.core.resolution.pipelines.ResolutionPipeline;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created. There, you have it.
 */
public class RamlLoader {

    public static Document load(URL url) throws ExecutionException, InterruptedException {

        WebApiDocument document = (WebApiDocument) Raml10.parse(url.toString()).get();
        ValidationReport report = Raml10.validate(document).get();

        List<amf.client.validate.ValidationResult> results = report.results();
        if ( results.isEmpty()) {

            //return document;
            return (WebApiDocument) Raml10.parse(url.toString()).get();
        } else {
            results.forEach(r -> System.err.println(r.message()));
            throw new IllegalArgumentException();
        }
    }

    public static Document loadEdited(URL url) throws ExecutionException, InterruptedException {

        WebApiDocument document = (WebApiDocument) Raml10.parse(url.toString()).get();
        ValidationReport report = Raml10.validate(document).get();

        List<amf.client.validate.ValidationResult> results = report.results();
        if ( results.isEmpty()) {

            Document parsedDocument =  (WebApiDocument) Raml10.parse(url.toString()).get();
            return (Document) new Raml10Resolver().resolve(parsedDocument, ResolutionPipeline.EDITING_PIPELINE());
        } else {
            results.forEach(r -> System.err.println(r.message()));
            throw new IllegalArgumentException();
        }
    }

    public static <T extends Shape> T findShape(final String name, List<DomainElement> types) {
        return types.stream().filter(x -> x instanceof Shape).map(x -> (T)x).filter(input -> input.name().value().equals(name)).findFirst().get();
    }

}
