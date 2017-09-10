package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class PropertyValueBuilder implements NodeBuilder, SupportsProperties<PropertyValueBuilder> {


    private String name;
    private final String value;
    private final String[] values;
    private PropertyValueBuilder subValue;

    public PropertyValueBuilder(String name, String value) {
        this.name = name;
        this.value = value;
        this.values = null;
        this.subValue = null;
    }

    public PropertyValueBuilder(String name, String[] values) {
        this.name = name;
        this.values = values;
        this.value = null;
        this.subValue = null;
    }

    public PropertyValueBuilder(String name) {

        this.name = name;
        this.subValue = null;
        this.values = null;
        this.value = null;
    }

    public static PropertyValueBuilder property(String name, String value) {

        return  new PropertyValueBuilder(name, value);
    }

    public static PropertyValueBuilder property(String name, String... values) {

        return  new PropertyValueBuilder(name, values);
    }

    public static PropertyValueBuilder property(String name){

        return new PropertyValueBuilder(name);
    }

    public PropertyValueBuilder withPropertyValue(PropertyValueBuilder values) {

        this.subValue = values;
        return this;
    }


    @Override
    public KeyValueNode buildNode() {

        if ( value != null ) {
            return new KeyValueNodeImpl(new StringNodeImpl(name), new StringNodeImpl(value));
        } else {

            if (values != null) {
                SimpleArrayNode node = new SimpleArrayNode();
                for (String value : values) {
                    node.addChild(new StringNodeImpl(value));
                }
                return new KeyValueNodeImpl(new StringNodeImpl(name), node);
            } else {

                return new KeyValueNodeImpl(new StringNodeImpl(name), subValue.buildNode());
            }
        }
    }
}
