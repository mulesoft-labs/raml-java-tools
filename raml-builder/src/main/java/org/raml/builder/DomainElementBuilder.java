package org.raml.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
abstract public class DomainElementBuilder<B extends DomainElementBuilder> implements NodeBuilder {

    private static int currentId =0 ;
    final private String id = "amf://id#" + Integer.toString(currentId ++);
    private List<NodeBuilder> builders = new ArrayList<>();

    public B with(NodeBuilder... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return (B) this;
    }

    public String id() {
        return id;
    }
}
