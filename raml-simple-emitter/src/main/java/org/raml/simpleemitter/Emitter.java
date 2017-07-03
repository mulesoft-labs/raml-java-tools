package org.raml.simpleemitter;

import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.api.ModifiableApi;
import org.raml.simpleemitter.nodes.VisitableApi;
import org.raml.v2.api.model.v10.api.Api;

import java.io.IOException;

/**
 * Created by jpbelang on 2017-06-25.
 */
public class Emitter {

    public void emit(Api api) throws IOException {

        ModifiableApi augApi = Augmenter.augment(ModifiableApi.class, api);
        augApi.visit(new SimpleOutputVisitor());
    }

}
