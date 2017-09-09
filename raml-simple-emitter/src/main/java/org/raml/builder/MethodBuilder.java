package org.raml.builder;

import org.raml.yagi.framework.nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class MethodBuilder extends KeyValueNodeBuilder<MethodBuilder> implements AnnotableBuilder<MethodBuilder> {

    private List<ResponseBuilder> responses = new ArrayList<>();
    private List<BodyBuilder> bodies = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    private MethodBuilder(String name) {
        super(name);
    }

    static public MethodBuilder method(String name) {

        return new MethodBuilder(name);
    }

    public MethodBuilder withResponses(ResponseBuilder... response) {

        responses.addAll(Arrays.asList(response));
        return this;
    }

    public MethodBuilder withBodies(BodyBuilder... builder) {

        this.bodies.addAll(Arrays.asList(builder));
        return this;
    }

    @Override
    public MethodBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public KeyValueNode buildNode() {
        KeyValueNode node =  super.buildNode();

        if ( ! responses.isEmpty()) {
            ObjectNodeImpl responsesValueNode = new ObjectNodeImpl();
            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("responses"), responsesValueNode);

            for (ResponseBuilder response : responses) {

                responsesValueNode.addChild(response.buildNode());
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

            for (BodyBuilder body : bodies) {
                bodyValueNode.addChild(body.buildNode());
            }
        }

        return node;

    }
}
