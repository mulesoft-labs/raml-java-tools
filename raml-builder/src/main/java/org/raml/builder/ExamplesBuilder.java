package org.raml.builder;

import amf.client.model.domain.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ExamplesBuilder extends DomainElementBuilder<ExamplesBuilder> implements SupportsProperties<ExamplesBuilder> {

    private boolean strict = true;
    private List<PropertyValueBuilder> propertyValues = new ArrayList<>();

    private ExamplesBuilder(String name) {
        super();
    }

    static public ExamplesBuilder example(String name) {

        return new ExamplesBuilder(name);
    }

    public static ExamplesBuilder singleExample() {
        return new ExamplesBuilder(null);
    }

    public ExamplesBuilder strict(boolean strict) {

        this.strict = strict;
        return this;
    }

    public ExamplesBuilder withNoProperties() {

        this.propertyValues.clear();
        return this;
    }

    public ExamplesBuilder withPropertyValues(PropertyValueBuilder... values) {

        this.propertyValues.addAll(Arrays.asList(values));
        return this;
    }

    public ExamplesBuilder withPropertyValue(PropertyValueBuilder value) {

        this.propertyValues.add(value);
        return this;
    }

    @Override
    public Example buildNode() {

        Example example = new Example();
        example.withStrict(strict);

        propertyValues.forEach(ex -> example.withStructuredValue(ex.buildNode()));

        return example;
    }
}
