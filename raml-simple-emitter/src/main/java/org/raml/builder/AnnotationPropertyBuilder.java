package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class AnnotationPropertyBuilder implements NodeBuilder {


    private String name;
    private final String value;
    private final String[] values;

    public AnnotationPropertyBuilder(String name, String value) {
        this.name = name;
        this.value = value;
        this.values = null;
    }

    public AnnotationPropertyBuilder(String name, String[] values) {
        this.name = name;
        this.values = values;
        this.value = null;
    }

    public static AnnotationPropertyBuilder property(String name, String value) {

        return  new AnnotationPropertyBuilder(name, value);
    }

    public static AnnotationPropertyBuilder property(String name, String... values) {

        return  new AnnotationPropertyBuilder(name, values);
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
            }
        }

        throw new RuntimeException("no value ?");
    }
}
