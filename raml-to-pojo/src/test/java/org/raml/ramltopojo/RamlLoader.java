package org.raml.ramltopojo;

import amf.client.model.domain.DomainElement;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.Shape;
import amf.client.validate.ValidationReport;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created. There, you have it.
 */
public class RamlLoader {
    public static Api load(InputStream is, String directory) {

        RamlModelResult ramlModelResult =
                new RamlModelBuilder().buildApi(
                        new InputStreamReader(is), directory);
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV10();
        }
    }

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

    public static NodeShape findTypes(final String name, List<TypeDeclaration> types) {
        return (NodeShape) types.stream().filter(input -> input.name().equals(name)).findFirst().get();
    }

    public static <T extends Shape> T findShape(final String name, List<DomainElement> types) {
        return types.stream().filter(x -> x instanceof Shape).map(x -> (T)x).filter(input -> input.name().value().equals(name)).findFirst().get();
    }

}
