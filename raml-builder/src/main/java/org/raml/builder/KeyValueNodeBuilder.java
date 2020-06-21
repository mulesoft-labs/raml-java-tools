package org.raml.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
abstract public class KeyValueNodeBuilder<B extends KeyValueNodeBuilder> implements NodeBuilder {

    final private String id;
    private List<NodeBuilder> builders = new ArrayList<>();

    protected KeyValueNodeBuilder(String name) {
        this.id = name;
    }

    protected KeyValueNodeBuilder(Long value) {
        this.id = value.toString();
    }

    public B with(NodeBuilder... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return (B) this;
    }
}
