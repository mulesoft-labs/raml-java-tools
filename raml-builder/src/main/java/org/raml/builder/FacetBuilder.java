package org.raml.builder;

import amf.client.model.domain.DomainElement;


/**
 * Created. There, you have it.
 */
public class FacetBuilder extends DomainElementBuilder<DomainElement, FacetBuilder> {

    private ValueNodeFactory value;

    public FacetBuilder(String name) {
        super();
    }

    public static FacetBuilder facet(String name) {

        return new FacetBuilder(name);
    }

    public FacetBuilder ofType(String typeName) {

      //  this.value = ValueNodeFactories.create(typeName);
        return this;
    }

    @Override
    protected DomainElement buildNodeLocally() {
        return null;
    }

}
