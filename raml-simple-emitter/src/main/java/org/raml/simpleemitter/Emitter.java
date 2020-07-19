package org.raml.simpleemitter;

import org.raml.v2.api.model.v10.api.Api;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.Node;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created. There, you have it.
 */
public class Emitter {

    final private HandlerList list;

    public Emitter(HandlerList list) {
        this.list = list;
    }

    public Emitter() {

        list = new HandlerList();
    }

    public void emit(WebApiDocument api) throws IOException {

        emit(api, new OutputStreamWriter(System.out));
    }

    public void emit(WebApiDocument api, Writer w) throws IOException {

        try {
            w.write(Raml10.generateString(api).get());
        } catch (InterruptedException|ExecutionException e) {
            throw new IOException(e);
        }
    }

}
