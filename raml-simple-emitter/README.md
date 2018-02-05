# raml-emitter

The simplest way to use this code:
```java
public class OtherMain {

    public static void main(String[] args) throws IOException {
        URL url = Main.class.getResource("fun.raml");
        Reader reader = new InputStreamReader(url.openStream());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(reader, url.getFile());
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.err.println(validationResult);
            }
        } else {

            Api realApi = ramlModelResult.getApiV10();
            Emitter emitter = new Emitter();
            emitter.emit(realApi);
            
            StringWriter sw = new StringWriter();
            emitter.emit(realApi, sw);

        }
    }
}
```

That's the gist of it. I'd like to add:

* Filtering on "path" and node type.
* Controlling the output (which is pretty verbose right now).

