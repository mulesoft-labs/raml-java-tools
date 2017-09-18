package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ResponseBuilder extends KeyValueNodeBuilder<ResponseBuilder> implements NodeBuilder, AnnotableBuilder<ResponseBuilder> {

    private List<BodyBuilder> bodies = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    private ResponseBuilder(int code) {
        super((long) code);
    }

    static public ResponseBuilder response(int code) {

        return new ResponseBuilder(code);
    }

    public ResponseBuilder withBodies(BodyBuilder... builder) {

        this.bodies.addAll(Arrays.asList(builder));
        return this;
    }

    @Override
    public ResponseBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }


    @Override
    public KeyValueNode buildNode() {
        KeyValueNode node =  super.buildNode();

        if ( ! bodies.isEmpty()) {
            ObjectNodeImpl valueNode = new ObjectNodeImpl();
            KeyValueNodeImpl bkvn = new KeyValueNodeImpl(new StringNodeImpl("body"), valueNode);
            node.getValue().addChild(bkvn);

            for (BodyBuilder body : bodies) {
                valueNode.addChild(body.buildNode());
            }
        }

        if ( ! annotations.isEmpty() ) {

            for (AnnotationBuilder annotation : annotations) {
                node.getValue().addChild(annotation.buildNode());
            }
        }

        return node;

    }
}
