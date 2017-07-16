package org.raml.builder;

import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.*;
import org.raml.yagi.framework.nodes.snakeyaml.SYStringNode;

/**
 * Created. There, you have it.
 */
public class Modification {

    static public<T> T set(T t, String tag, String value) {

        Node n = ((NodeModel)t).getNode();
        StringNode node = new StringNodeImpl(tag);
        KeyValueNode kvn = new KeyValueNodeImpl();
        kvn.addChild(node);
        kvn.addChild(new StringNodeImpl(value));
        n.addChild(kvn);
        return t;
    }

    static public<T,V> T add(T t, V add) {

        ((NodeModel)t).getNode().addChild(((NodeModel)add).getNode());
        return t;
    }
}
