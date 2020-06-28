package org.raml.builder;

import amf.client.model.domain.DomainElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created. There, you have it.
 */
abstract public class DomainElementBuilder<T extends DomainElement, B extends DomainElementBuilder<T, B>> implements NodeBuilder<T> {

    private static int currentId =0 ;
    final private String id = "amf://id#" + currentId++;
    private List<NodeBuilder<?>> builders = new ArrayList<>();

    private Supplier<T> response = this::buildNodeLocally;

    public B with(NodeBuilder<?>... builders) {

        this.builders.addAll(Arrays.asList(builders));
        return (B) this;
    }

    protected void commonNodeInfo(T node) {

        node.withId(id);
    }

    protected abstract T buildNodeLocally();

    @Override
    public T buildNode() {
        return response.get();
    }

    public String id() {
        return id;
    }
}
