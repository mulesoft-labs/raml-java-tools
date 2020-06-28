package org.raml.builder;

import amf.client.model.domain.DomainElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class AnnotationBuilder extends DomainElementBuilder<DomainElement, AnnotationBuilder> implements NodeBuilder<DomainElement> {

    private List<PropertyValueBuilder> properties = new ArrayList<>();

    private AnnotationBuilder(String name) {
        super();
    }

    static public AnnotationBuilder annotation(String name) {

        return new AnnotationBuilder(name);
    }


    public AnnotationBuilder withProperties(PropertyValueBuilder...builders) {

        this.properties.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public DomainElement buildNodeLocally() {

        return null;
    }
}
