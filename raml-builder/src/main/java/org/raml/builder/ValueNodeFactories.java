package org.raml.builder;

import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;

/**
 * Created. There, you have it.
 */
public class ValueNodeFactories {

    static ValueNodeFactory create(final long value) {

        return new ValueNodeFactory() {
            @Override
            public Node createNode() {
                return new NumberNode(value);
            }
        };
    }

    static ValueNodeFactory create(final boolean value) {

        return new ValueNodeFactory() {
            @Override
            public Node createNode() {
                return new BooleanNode(value);
            }
        };
    }

    static ValueNodeFactory create(final String value) {

        return new ValueNodeFactory() {
            @Override
            public Node createNode() {
                return new StringNodeImpl(value);
            }
        };
    }

    static ValueNodeFactory create(final boolean[] values) {

        return create(new SimpleArrayNode(), values);
    }

    static ValueNodeFactory create(final long[] values) {

        return create(new SimpleArrayNode(), values);
    }

    static ValueNodeFactory create(final String[] values) {

        return create(new SimpleArrayNode(), values);
    }

    static ValueNodeFactory create(final ArrayNode node, final boolean[] values) {

        return new ValueNodeFactory() {
            @Override
            public Node createNode() {
                for (boolean value : values) {
                    node.addChild(new BooleanNode(value));
                }

                return node;
            }
        };
    }

    static ValueNodeFactory create(final ArrayNode node, final long[] values) {

        return new ValueNodeFactory() {
            @Override
            public Node createNode() {
                for (long value : values) {
                    node.addChild(new NumberNode(value));
                }

                return node;
            }
        };
    }

    static ValueNodeFactory create(final ArrayNode node, final String[] values) {

        return new ValueNodeFactory() {
            @Override
            public Node createNode() {
                for (String value : values) {
                    node.addChild(new StringNodeImpl(value));
                }

                return node;
            }
        };
    }

}
