package org.raml.simpleemitter;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.simpleemitter.handlers.*;
import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class HandlerList extends NodeHandler<Node> {

    private final List<NodeHandler<? extends Node>> handlerList;

    public HandlerList(List<NodeHandler<? extends Node>> handlers) {

        this.handlerList = handlers;
    }

    public HandlerList() {
        handlerList = new ArrayList<>();

      //  handlerList.add(new TypeDeclarationNodeHandler(this));
        handlerList.add(new TypeExpressionNodeHandler(this));
        handlerList.add(new SimpleTypeNodeHandler());
        handlerList.add(new KeyValueNodeHandler(this));
        handlerList.add(new ObjectNodeHandler(this));
        handlerList.add(new ArrayNodeHandler(this));
        handlerList.add(new NullNodeHandler(this));
        handlerList.add(new ReferenceNodeHandler(this));
        handlerList.add(new DefaultNodeHandler());
    }

    @Override
    public boolean handles(final Node node) {

        return FluentIterable.from(handlerList).anyMatch(new Predicate<NodeHandler<? extends Node>>() {
            @Override
            public boolean apply(@Nullable NodeHandler<? extends Node> nodeHandler) {
                return nodeHandler.handles(node);
            }
        });
    }

    @Override
    public boolean handleSafely(final Node node, YamlEmitter emitter) throws IOException {

        Optional<NodeHandler<?>> handler = FluentIterable.from(handlerList).firstMatch(new Predicate<NodeHandler<? extends Node>>() {
            @Override
            public boolean apply(@Nullable NodeHandler<? extends Node> nodeHandler) {
                return nodeHandler.handles(node);
            }
        });

        return handler.isPresent() && handler.get().handle(node, emitter);

    }
}
