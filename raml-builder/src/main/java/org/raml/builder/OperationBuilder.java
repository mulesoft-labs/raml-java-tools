package org.raml.builder;

import amf.client.model.domain.Operation;
import amf.client.model.domain.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class OperationBuilder extends DomainElementBuilder<OperationBuilder> implements AnnotableBuilder<OperationBuilder>/*, ModelBuilder<Method>*/ {

    private final String name;
    private List<ResponseBuilder> responses = new ArrayList<>();
    private List<PayloadBuilder> bodies = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();
    private List<ParameterBuilder> queryParameters = new ArrayList<>();
    private List<ParameterBuilder> headerParameters = new ArrayList<>();
    private String description;


    private OperationBuilder(String name) {
        super();
        this.name = name;
    }

    static public OperationBuilder method(String name) {

        return new OperationBuilder(name);
    }

    public OperationBuilder withResponses(ResponseBuilder... response) {

        responses.addAll(Arrays.asList(response));
        return this;
    }

    public OperationBuilder withPayloads(PayloadBuilder... builder) {

        this.bodies.addAll(Arrays.asList(builder));
        return this;
    }

    public OperationBuilder withQueryParameter(ParameterBuilder... builder) {

        this.queryParameters.addAll(Arrays.asList(builder));
        return this;
    }

    public OperationBuilder withHeaderParameters(ParameterBuilder... builder) {

        this.headerParameters.addAll(Arrays.asList(builder));
        return this;
    }

    @Override
    public OperationBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public Operation buildNode() {

        Operation node =  new Operation();
        node.withMethod(name);
        Optional.ofNullable(description).ifPresent(node::withDescription);
        node.withResponses(responses.stream().map(ResponseBuilder::buildNode).collect(Collectors.toList()));

        Request request = new Request();
        node.withRequest(request);
        request.withQueryParameters(queryParameters.stream().map(ParameterBuilder::buildNode).collect(Collectors.toList()));
        request.withHeaders(headerParameters.stream().map(ParameterBuilder::buildNode).collect(Collectors.toList()));
        request.withPayloads(bodies.stream().map(PayloadBuilder::buildNode).collect(Collectors.toList()));


//
//        if ( ! annotations.isEmpty() ) {
//
//            for (AnnotationBuilder annotation : annotations) {
//                node.getValue().addChild(annotation.buildNode());
//            }
//        }

        return node;
    }

    public OperationBuilder description(String description) {
        this.description = description;
        return this;
    }
}
