package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.document.Module;
import amf.client.model.domain.AnyShape;
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

            Document parsedDocument =  (WebApiDocument) Raml10.parse(url.toString()).get();
            // ok, the document is parsed.  I'm not going to mark all the superclass names + the inline types.
            markAll(parsedDocument);
            return (Document) new Raml10Resolver().resolve(parsedDocument, ResolutionPipeline.EDITING_PIPELINE());
        } else {
            results.forEach(r -> System.err.println(r.message() + ":::" + r.position()));
            throw new IllegalArgumentException();
        }
    }

    public static Document load(String raml) throws ExecutionException, InterruptedException {

        WebApiDocument document = (WebApiDocument) Raml10.parse(raml).get();
        ValidationReport report = Raml10.validate(document).get();

        List<amf.client.validate.ValidationResult> results = report.results();
        if ( results.isEmpty()) {

            Document parsedDocument =  (WebApiDocument) Raml10.parse(raml).get();
            // ok, the document is parsed.  I'm not going to mark all the superclass names + the inline types.
            markAll(parsedDocument);
            return (Document) new Raml10Resolver().resolve(parsedDocument, ResolutionPipeline.EDITING_PIPELINE());
        } else {
            results.forEach(r -> System.err.println(r.message()));
            throw new IllegalArgumentException();
        }
    }

    private static void markAll(Document parsedDocument) {
        parsedDocument.findByType("http://a.ml/vocabularies/shapes#Shape").stream()
                .filter(x -> x instanceof AnyShape)
                .map(AnyShape.class::cast)
                .forEach(ExtraInformation::createInformation);

        parsedDocument.references().stream()
                .filter(x -> x instanceof Module)
                .map(x -> (Module) x)
                .flatMap(TypeFindingUtils::gettingSubModules)
                .forEach(d -> d.findByType("http://a.ml/vocabularies/shapes#Shape").stream()
                        .filter(x -> x instanceof AnyShape)
                        .map(AnyShape.class::cast)
                        .forEach(ExtraInformation::createInformation));
    }

    public static <T extends Shape> T findShape(final String name, List<DomainElement> types) {
        return types.stream().filter(x -> x instanceof Shape).map(x -> (T)x).filter(input -> input.name().value().equals(name)).findFirst().get();
    }

}
