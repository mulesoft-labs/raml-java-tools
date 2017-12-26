package org.raml.ramltopojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ResultingPojos {

    private final List<CreationResult> results = new ArrayList<>();
    private final GenerationContextImpl generationContext;

    public ResultingPojos(GenerationContextImpl generationContext) {

        this.generationContext = generationContext;
    }

    public void addNewResult(CreationResult spec) {

        this.results.add(spec);
    }

    public List<CreationResult> creationResults() {
        return results;
    }

    public void createFoundTypes(String rootDirectory) throws IOException {

        for (CreationResult result : results) {
            result.createType(rootDirectory);
        }
    }

    public void createAllTypes(String rootDirectory) throws IOException {

        for (CreationResult result : results) {
            result.createType(rootDirectory);
        }

        generationContext.createTypes(rootDirectory);
    }

}
