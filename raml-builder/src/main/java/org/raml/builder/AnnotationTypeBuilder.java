package org.raml.builder;

import amf.client.model.domain.DomainElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class AnnotationTypeBuilder extends DomainElementBuilder<DomainElement, AnnotationBuilder> {

    private List<NodeBuilder> properties = new ArrayList<>();

    private AnnotationTypeBuilder(String name) {
        super();
    }

    static public AnnotationTypeBuilder annotationType(String name) {

        return new AnnotationTypeBuilder(name);
    }

    @Override
    public DomainElement buildNodeLocally() {

      return null;
    }

    public AnnotationTypeBuilder withProperty(NodeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }

}
