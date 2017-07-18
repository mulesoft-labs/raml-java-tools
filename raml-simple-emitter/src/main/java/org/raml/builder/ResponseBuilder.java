package org.raml.builder;

import org.raml.v2.api.model.v10.bodies.Response;

/**
 * Created. There, you have it.
 */
public class ResponseBuilder extends KeyValueNodeBuilder<Response, ResponseBuilder> implements NodeBuilder {

    private ResponseBuilder(int code) {
        super((long) code);
    }

    static public ResponseBuilder response(int code) {

        return new ResponseBuilder(code);
    }

    public Response build() {

        return super.build(Response.class, buildNode());
    }
}
