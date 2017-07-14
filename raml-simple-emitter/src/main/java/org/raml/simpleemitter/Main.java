package org.raml.simpleemitter;


import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;


import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Created by jpbelang on 2017-06-25.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        //URL url = Main.class.getResource("api.raml");
        URL url = Main.class.getResource("fun.raml");

        Reader reader = new InputStreamReader(url.openStream());

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(reader, url.getFile());
        if (ramlModelResult.hasErrors())
        {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults())
            {
                System.err.println(validationResult);
            }
        }
        else
        {
            Api api = ramlModelResult.getApiV10();
            System.err.println(api);
            AnotherEmitter emitter = new AnotherEmitter();
            emitter.emit(api);

        }
    }
}
