package org.raml.simpleemitter;

import org.raml.parsertools.Augmenter;
import org.raml.v2.api.model.v10.api.Api;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by jpbelang on 2017-06-25.
 */
public class Emitter {

    public void emit(Api api) throws IOException {

        RamlEmitter emitter = Augmenter.augment(RamlEmitter.class, api);
        emitter.emit(new OutputStreamWriter(System.out));
    }
}
