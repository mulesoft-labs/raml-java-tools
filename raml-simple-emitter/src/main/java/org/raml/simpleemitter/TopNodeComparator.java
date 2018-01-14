package org.raml.simpleemitter;

import com.google.common.collect.ImmutableMap;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import java.util.Comparator;
import java.util.Map;

/**
 * Created. There, you have it.
 */
class TopNodeComparator implements Comparator<Node> {

    private Map<String, Integer> order = ImmutableMap.of(
            "title", 0,
            "baseUri", 1,
            "mediaType", 2,
            "version", 3
    );

    @Override
    public int compare(Node o1, Node o2) {
        if (o1 instanceof KeyValueNode && o2 instanceof KeyValueNode) {

            return compareKeys(((KeyValueNode) o1).getKey(), ((KeyValueNode) o2).getKey());
        }

        if (o1 instanceof KeyValueNode) {

            return -1;
        }

        return 1;
    }

    int compareKeys(Node key1, Node key2) {

        if (key1 instanceof SimpleTypeNode && key2 instanceof SimpleTypeNode) {

            String v1 = ((SimpleTypeNode) key1).getLiteralValue();
            String v2 = ((SimpleTypeNode) key2).getLiteralValue();
            if (order.containsKey(v1) && order.containsKey(v2)) {

                return order.get(v1) - order.get(v2);
            }

            if (order.containsKey(v1)) {
                return -1;
            }

            if (order.containsKey(v2)) {
                return 1;
            }

            if (v1.startsWith("/") && !v2.startsWith("/")) {
                return 1;
            }

            if (!v1.startsWith("/") && v2.startsWith("/")) {
                return -1;
            }

            return v1.compareTo(v2);
        }

        if (key1 instanceof SimpleTypeNode) {

            return -1;
        }

        if (key2 instanceof SimpleTypeNode) {

            return 1;
        }

        return 0;
    }
}
