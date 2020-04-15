package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.document.Module;
import amf.client.model.domain.AnyShape;
import amf.client.model.domain.DomainElement;
import amf.client.model.domain.Shape;
import amf.client.resolve.Raml08Resolver;
import amf.client.resolve.Raml10Resolver;
import amf.client.validate.ValidationReport;
import amf.core.resolution.pipelines.ResolutionPipeline;
import webapi.Raml08;
import webapi.Raml10;
import webapi.WebApiBaseUnit;
import webapi.WebApiDocument;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Created. There, you have it.
 */
public class RamlLoader {

    public interface Loader {

        Document load(String raml) throws RamlLoaderException;
    }

    public static Document load(URL url) throws RamlLoaderException {

        return load(url.toString());
    }


    public static Document load(String raml) throws RamlLoaderException {

        return load(raml, Raml10::parse, Raml10::validate, RamlLoader::raml10Resolve);
    }

    public static Document load08(String raml) throws RamlLoaderException {

        return load(raml, Raml08::parse, Raml08::validate, RamlLoader::raml08Resolve);
    }

    public static Document load(String raml, Function<String, CompletableFuture<WebApiBaseUnit>> parser, Function<WebApiDocument, CompletableFuture<ValidationReport>> validator, Function<Document, Document> resolver) throws RamlLoaderException {

        try {
            WebApiDocument document = (WebApiDocument) parser.apply(raml).get();
            ValidationReport report = validator.apply(document).get();
            List<amf.client.validate.ValidationResult> results = report.results();
            if (results.isEmpty()) {

                Document parsedDocument = (Document) parser.apply(raml).get();
                // ok, the document is parsed.  I'm not going to mark all the superclass names + the inline types.
                markAll(parsedDocument);
                return resolver.apply(parsedDocument);
            } else {
                results.forEach(r -> System.err.println(r.message()));
                throw new RamlLoaderException(results);
            }
        } catch (InterruptedException|ExecutionException e) {

            throw new RamlLoaderException(e);
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

    private static Document raml10Resolve(Document d) {
        return (Document) new Raml10Resolver().resolve(d, ResolutionPipeline.EDITING_PIPELINE());
    }

    private static Document raml08Resolve(Document d) {
        return (Document) new Raml08Resolver().resolve(d, ResolutionPipeline.EDITING_PIPELINE());
    }



}
