package org.raml.simpleemitter.handlers;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ArrayNodeHandler extends NodeHandler<ArrayNode> {


    private final HandlerList handlerList;

    public ArrayNodeHandler(HandlerList handlerList) {
        this.handlerList = handlerList;
    }

    @Override
    public boolean handles(Node node) {

        return node instanceof ArrayNode;
    }

    @Override
    public boolean handleSafely(ArrayNode node, YamlEmitter emitter) throws IOException {


        List<Node> children = node.getChildren();
        if ( childrenAreAllScalarTypes(children)) {
            emitter.writeSyntaxElement("[");
            for (int a = 0; a < children.size(); a++) {

                handlerList.handle(children.get(a), emitter);
                if (a < children.size() - 1) {
                    emitter.writeSyntaxElement(",");
                }
            }
            emitter.writeSyntaxElement("]");
        } else {

            YamlEmitter indented = emitter.indent();
            for (Node child : children) {

                indented.writeSyntaxElement("\n");
                indented.writeIndent();
                indented.writeSyntaxElement("- ");
                handlerList.handle(child, indented.bulletListArray());
            }
        }

        return true;
    }

    private boolean childrenAreAllScalarTypes(List<Node> children) {
        return FluentIterable.from(children).allMatch(new Predicate<Node>() {
            @Override
            public boolean apply(@Nullable Node node) {
                return node instanceof SimpleTypeNode;
            }
        });
    }
}
