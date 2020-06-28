package org.raml.builder;

import amf.client.model.domain.ArrayNode;
import amf.client.model.domain.DataNode;
import amf.client.model.domain.ObjectNode;
import amf.client.model.domain.ScalarNode;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created. There, you have it.
 */
public class PropertyValueBuilder extends DomainElementBuilder<DataNode, PropertyValueBuilder> implements SupportsProperties<PropertyValueBuilder> {


    private final DataNode node;
    private String name;
    private final ValueNodeFactory value;
    private PropertyValueBuilder subValue;

    private PropertyValueBuilder(String name, ValueNodeFactory value) {
        this.name = name;
        this.value = value;
        this.subValue = null;
        this.node = null;
    }

    private PropertyValueBuilder(String name) {

        this.name = name;
        this.subValue = null;
        this.value = null;
        this.node = null;
    }

    private PropertyValueBuilder(DataNode node) {

        this.node = node;
        this.value = null;
    }

    public static PropertyValueBuilder property(String name, String value) {

        return new PropertyValueBuilder(new ScalarNode(value, "http://www.w3.org/2001/XMLSchema#string").withName(name));
    }

    public static PropertyValueBuilder property(String name, long value) {

        return new PropertyValueBuilder(new ScalarNode(Long.toString(value), "http://www.w3.org/2001/XMLSchema#long").withName(name));
    }

    public static PropertyValueBuilder property(String name, boolean value) {

        return new PropertyValueBuilder(new ScalarNode(Boolean.toString(value), "http://www.w3.org/2001/XMLSchema#boolean").withName(name));
    }

    public static PropertyValueBuilder propertyOfArray(String name, String... values) {

        ArrayNode arrayNode = new ArrayNode();
        arrayNode.withName(name);

        Arrays.stream(values).map(s -> new ScalarNode(s, "http://www.w3.org/2001/XMLSchema#string")).forEach(arrayNode::addMember);
        return new PropertyValueBuilder(arrayNode);
    }

    public static PropertyValueBuilder propertyOfArray(String name, long... values) {

        ArrayNode arrayNode = new ArrayNode();
        arrayNode.withName(name);
        Arrays.stream(values).mapToObj(s -> new ScalarNode(Long.toString(s), "http://www.w3.org/2001/XMLSchema#long")).forEach(arrayNode::addMember);;
        return new PropertyValueBuilder(arrayNode);
    }

    public static PropertyValueBuilder propertyOfArray(String name, boolean... values) {

        ArrayNode arrayNode = new ArrayNode();
        arrayNode.withName(name);

        IntStream.range(0, values.length)
                .mapToObj(idx -> values[idx])
                .map(x -> Boolean.toString(x))
                .map(x -> new ScalarNode(x, "http://www.w3.org/2001/XMLSchema#boolean")).forEach(arrayNode::addMember);
        return new PropertyValueBuilder(arrayNode);
    }

    public static PropertyValueBuilder property(String name) {

        return new PropertyValueBuilder(name);
    }

    public PropertyValueBuilder withPropertyValue(PropertyValueBuilder values) {

        this.subValue = values;
        return this;
    }


    @Override
    public DataNode buildNode() {

        if (value != null) {
            return node;
        } else {

            if (subValue != null) {
                return ((ObjectNode)node).addProperty(name, subValue.buildNode());
            } else {

                return new ObjectNode().withName(name);
            }
        }
    }
}
