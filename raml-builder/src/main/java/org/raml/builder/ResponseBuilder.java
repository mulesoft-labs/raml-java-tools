package org.raml.builder;

import amf.client.model.domain.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class ResponseBuilder extends DomainElementBuilder<Response, ResponseBuilder> implements AnnotableBuilder<ResponseBuilder> {

    private final int code;
    private List<PayloadBuilder> bodies = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();
    private String description;

    private ResponseBuilder(int code) {
        super();
        this.code = code;
    }

    static public ResponseBuilder response(int code) {

        return new ResponseBuilder(code);
    }

    public ResponseBuilder withBodies(PayloadBuilder... builder) {

        this.bodies.addAll(Arrays.asList(builder));
        return this;
    }

    @Override
    public ResponseBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }


    @Override
    public Response buildNodeLocally() {
        Response node =  new Response();
        node.withStatusCode(Integer.toString(code));
        Optional.ofNullable(description).ifPresent(node::withDescription);

        node.withPayloads(bodies.stream().map(PayloadBuilder::buildNodeLocally).collect(Collectors.toList()));


//        if ( ! annotations.isEmpty() ) {
//
//            for (AnnotationBuilder annotation : annotations) {
//                node.getValue().addChild(annotation.buildNode());
//            }
//        }

        return node;

    }

    public ResponseBuilder description(String description) {
        this.description = description;
        return this;
    }
}
