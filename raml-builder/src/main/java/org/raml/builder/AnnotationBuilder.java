package org.raml.builder;

import amf.client.model.domain.DomainElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class AnnotationBuilder extends KeyValueNodeBuilder<AnnotationBuilder> implements NodeBuilder {

    private List<PropertyValueBuilder> properties = new ArrayList<>();

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


    public AnnotationBuilder withProperties(PropertyValueBuilder...builders) {

        this.properties.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public DomainElement buildNode() {

        KeyValueNode k = super.buildNode();
        for (PropertyValueBuilder property : properties) {
            k.getValue().addChild(property.buildNode());
        }

        return k;
    }
}
