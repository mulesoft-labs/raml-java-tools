package org.raml.simpleemitter;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Created. There, you have it.
 */
public class OtherMain {

    public static void main(String[] args) throws IOException {
        // URL url = Main.class.getResource("api.raml");
        URL url = Main.class.getResource("fun.raml");

        Reader reader = new InputStreamReader(url.openStream());

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(reader, url.getFile());
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.err.println(validationResult);
            }
        } else {

            Api realApi = ramlModelResult.getApiV10();
            System.err.println("grr " + realApi.annotationTypes().get(0).name());
        }
    }
}
