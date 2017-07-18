# raml-java-tools

## A simple emitter

This is the first draft of an emitter for raml documents (version 2).  I don't have enough tests for it
yet.  These are coming.

Usage:

```java
        URL url = Main.class.getResource("api.raml");

        Reader reader = new InputStreamReader(url.openStream());

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(reader, url.getFile());
        Emitter emitter = new Emitter();
        emitter.emit(api);
```

## A simple builder

I also have something to build a document from scratch. 

```java
            Emitter emitter = new Emitter();
            emitter.emit(api);
            Api api = document().with(
                    key("title", "Hello!"),
                    resource("/yes")
                            .with(
                                    key("displayName", "I'm happy"),
                                    resource("/no").with(
                                            key("displayName", "I'm happy"),
                                            method("get").with(
                                                    key("description", "Hello")
                                            ).withResponse(response(200))
                                    )
                            )).build();

            // here, we verify the structure by running the grammar on the resulting tree.
            // this will be encapsulated into the ducument builder.  
            final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
            Node node = ((NodeModel) api).getNode();
            grammarPhase.apply(node);

            List<ErrorNode> errors = node.findDescendantsWith(ErrorNode.class);
            for (ErrorNode error : errors) {
                System.err.println("error: " + error.getErrorMessage());
            }
            if (errors.size() == 0) {
                emitter.emit(api);
            }

            // The objects seem to behave correctly.    
            System.out.println();
            System.out.println(api.title().value() + ", " + api.resources());
        }
    }
}
```

I also plan to allow for modifying a loaded model.  This is what I have.  I don't like it :-)

```java
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

            Emitter emitter = new Emitter();

            api = Modification.set(api, "version", "v123");

            Resource r = resource("/yes").with(

                key("displayName", "I'm happy")

            ).build();

            // Adding build resource to the api
            Modification.add(api, r);

            // validate again.
            final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
            Node node = ((NodeModel) api).getNode();
            grammarPhase.apply(node);

            List<ErrorNode> errors = node.findDescendantsWith(ErrorNode.class);
            for (ErrorNode error : errors) {
                System.err.println("error: " + error.getErrorMessage());
            }
            if ( errors.size() == 0 ) {
                emitter.emit(api);
            }
        }
    }
```