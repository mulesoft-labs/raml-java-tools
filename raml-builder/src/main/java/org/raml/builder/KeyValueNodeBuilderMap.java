package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class KeyValueNodeBuilderMap<T extends KeyValueNodeBuilder> {

    private final Map<String, T> map = new LinkedHashMap<>();

    public void addAll(T... builder) {
        for (T t : builder) {

            map.put(t.id(), t);
        }
    }

    public void addAll(List<T> builder) {
        for (T t : builder) {

            map.put(t.id(), t);
        }
    }

    static<T extends KeyValueNodeBuilder> KeyValueNodeBuilderMap<T> createMap() {

        return new KeyValueNodeBuilderMap<>();
    }

    public void addToParent(Node documentNode) {
        for (T resourceBuilder : map.values()) {

            documentNode.addChild(resourceBuilder.buildNode());
        }

    }

    public void addAllToNamedNode(String types, Node documentNode) {

        if ( map.values().size() > 0 ) {
            ObjectNodeImpl typesNode = new ObjectNodeImpl();
            KeyValueNodeImpl typesKvn = new KeyValueNodeImpl(new StringNodeImpl(types), typesNode);
            documentNode.addChild(typesKvn);
            addToParent(typesNode);
        }
    }
}
