package org.raml.ramltopojo.object;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created. There, you have it.
 */
public class RamlLoader {
    public static Api load(InputStream is) {

        RamlModelResult ramlModelResult =
                new RamlModelBuilder().buildApi(
                        new InputStreamReader(is), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV10();
        }
    }
}
