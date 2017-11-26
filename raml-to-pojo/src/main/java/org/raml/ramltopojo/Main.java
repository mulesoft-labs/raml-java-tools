package org.raml.ramltopojo;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;

import java.io.FileReader;

import static org.raml.ramltopojo.TypeFetchers.fromAnywhere;
import static org.raml.ramltopojo.TypeFinders.everyWhere;

/**
 * Created. There, you have it.
 */
public class Main {

    public static void main(String[] args) throws Exception  {

        RamlModelResult ramlModelResult =
                new RamlModelBuilder().buildApi(
                        new FileReader("/Users/jpbelang/IdeaProjects/raml-java-tools/raml-to-pojo/src/test/resources/org/raml/ramltopojo/union/union-primitive-type.raml"),
                        ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.err.println(validationResult.getMessage());
            }
            throw new Exception();
        }

        final Api api = ramlModelResult.getApiV10();
        RamlToPojo ramlToPojo = RamlToPojoBuilder.builder(api)
                .inPackage("my.packaging")
                .fetchTypes(fromAnywhere())
                .findTypes(everyWhere()).build();

        ramlToPojo.buildPojos().createAllTypes("/Users/jpbelang/IdeaProjects/raml-java-tools/garbage/src/main/java");
    }
}
