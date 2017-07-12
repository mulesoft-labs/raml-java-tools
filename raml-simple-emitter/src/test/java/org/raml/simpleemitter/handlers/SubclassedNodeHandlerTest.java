package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.NodeHandler;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;

import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class SubclassedNodeHandlerTest extends UnitTest {

    @Mock
    HandlerList subNodeHandler;

    @Mock
    TypeDeclarationNode node;

    @Mock
    YamlEmitter emitter;

    @Mock
    NodeHandler<ObjectNode> nodeVerifier;

    @Test
    public void fallsThrough() throws Exception {

        when(subNodeHandler.handle(node, emitter)).thenReturn(false);

        SubclassedNodeHandler<ObjectNode> on = new SubclassedNodeHandler<ObjectNode>(ObjectNode.class, subNodeHandler) {
            @Override
            public boolean handleSafely(ObjectNode node, YamlEmitter emitter) throws IOException {

                nodeVerifier.handleSafely(node, emitter);
                return true;
            }
        };

        assertTrue(on.handle(node, emitter));

        verify(subNodeHandler).handle(node, emitter);
        verify(nodeVerifier).handleSafely(node, emitter);
    }

    @Test
    public void sublistHandles() throws Exception {

        when(subNodeHandler.handle(node, emitter)).thenReturn(true);

        SubclassedNodeHandler<ObjectNode> on = new SubclassedNodeHandler<ObjectNode>(ObjectNode.class, subNodeHandler) {
            @Override
            public boolean handleSafely(ObjectNode node, YamlEmitter emitter) throws IOException {

                nodeVerifier.handleSafely(node, emitter);
                return true;
            }
        };

        assertTrue(on.handle(node, emitter));

        verify(subNodeHandler).handle(node, emitter);
        verify(nodeVerifier, never()).handleSafely(node, emitter);
    }

}