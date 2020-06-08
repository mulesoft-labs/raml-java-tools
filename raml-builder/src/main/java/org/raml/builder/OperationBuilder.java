package org.raml.builder;

import amf.client.model.domain.DomainElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class OperationBuilder extends KeyValueNodeBuilder<OperationBuilder> implements AnnotableBuilder<OperationBuilder>/*, ModelBuilder<Method>*/ {

    private List<ResponseBuilder> responses = new ArrayList<>();
    private List<PayloadBuilder> bodies = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();
    private List<ParameterBuilder> queryParameters = new ArrayList<>();
    private List<ParameterBuilder> headerParameters = new ArrayList<>();
    private String description;


    private OperationBuilder(String name) {
        super(name);
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

 //   @Override
 //   protected KeyValueNode createContainerNode() {
 //       return new MethodNode();
  //  }

    @Override
    public DomainElement buildNode() {
        KeyValueNode node =  super.buildNode();

        addProperty(node.getValue(), "description", description);

        if ( ! responses.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("responses"), responsesValueNode);

            for (ResponseBuilder response : responses) {

                responsesValueNode.addChild(response.buildNode());
            }

            node.getValue().addChild(kvn);
        }

        if ( ! queryParameters.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("queryParameters"), responsesValueNode);

            for (ParameterBuilder queryParameter : queryParameters) {

                responsesValueNode.addChild(queryParameter.buildNode());
            }

            node.getValue().addChild(kvn);
        }

        if ( ! headerParameters.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("headers"), responsesValueNode);

            for (ParameterBuilder quertParameter : headerParameters) {

                responsesValueNode.addChild(quertParameter.buildNode());
            }

            node.getValue().addChild(kvn);
        }

        if ( ! annotations.isEmpty() ) {

            for (AnnotationBuilder annotation : annotations) {
                node.getValue().addChild(annotation.buildNode());
            }
        }

        if ( ! bodies.isEmpty()) {
            ObjectNodeImpl bodyValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl bkvn = new KeyValueNodeImpl(new StringNodeImpl("body"), bodyValueNode);
            node.getValue().addChild(bkvn);

            for (PayloadBuilder body : bodies) {
                bodyValueNode.addChild(body.buildNode());
            }
        }

        return node;

    }

    public OperationBuilder description(String description) {
        this.description = description;
        return this;
    }
}
