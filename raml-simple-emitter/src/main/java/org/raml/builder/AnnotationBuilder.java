package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class AnnotationBuilder extends KeyValueNodeBuilder<AnnotationBuilder> implements NodeBuilder {

    private List<AnnotationPropertyBuilder> properties = new ArrayList<>();

    private AnnotationBuilder(String name) {
        super(name);
    }

    static public AnnotationBuilder annotation(String name) {

        return new AnnotationBuilder(name);
    }

    @Override
    protected StringNodeImpl createKeyNode(String id) {
        return new StringNodeImpl("(" + id + ")");
    }


    public AnnotationBuilder withProperties(AnnotationPropertyBuilder...builders) {

        this.properties.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public KeyValueNode buildNode() {

        KeyValueNode k = super.buildNode();
        for (AnnotationPropertyBuilder property : properties) {
            k.getValue().addChild(property.buildNode());
        }

        return k;
    }
}
