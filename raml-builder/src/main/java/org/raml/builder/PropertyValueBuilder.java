package org.raml.builder;

import amf.client.model.domain.DomainElement;
import amf.client.model.domain.ScalarNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class PropertyValueBuilder implements NodeBuilder, SupportsProperties<PropertyValueBuilder> {


    private String name;
    private final ValueNodeFactory value;
    private PropertyValueBuilder subValue;

    public PropertyValueBuilder(String name, ValueNodeFactory value) {
        this.name = name;
        this.value = value;
        this.subValue = null;
    }

    public PropertyValueBuilder(String name) {

        this.name = name;
        this.subValue = null;
        this.value = null;
    }

    public static PropertyValueBuilder property(String name, String value) {

        return new PropertyValueBuilder(name, ValueNodeFactories.create(value));
    }

    public static PropertyValueBuilder property(String name, long value) {

        return new PropertyValueBuilder(name, ValueNodeFactories.create(value));
    }

    public static PropertyValueBuilder property(String name, boolean value) {

        return new PropertyValueBuilder(name, ValueNodeFactories.create(value));
    }

    public static PropertyValueBuilder propertyOfArray(String name, String... values) {

        return new PropertyValueBuilder(name, ValueNodeFactories.create(values));
    }

    public static PropertyValueBuilder propertyOfArray(String name, long... values) {

        return new PropertyValueBuilder(name, ValueNodeFactories.create(values));
    }

    public static PropertyValueBuilder propertyOfArray(String name, boolean... values) {

        return new PropertyValueBuilder(name, ValueNodeFactories.create(values));
    }

    public static PropertyValueBuilder property(String name) {

        return new PropertyValueBuilder(name);
    }

    public PropertyValueBuilder withPropertyValue(PropertyValueBuilder values) {

        this.subValue = values;
        return this;
    }


    @Override
    public ScalarNode buildNode() {

        if (value != null) {
            return new KeyValueNodeImpl(new StringNodeImpl(name), value.createNode());
        } else {

            if (subValue != null) {
                return new KeyValueNodeImpl(new StringNodeImpl(name), subValue.buildNode());
            } else {

                return new KeyValueNodeImpl(new StringNodeImpl(name), new ObjectNodeImpl());
            }
        }
    }


}
