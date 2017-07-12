package org.raml.simpleemitter;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.Node;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class HandlerListTest extends UnitTest {

    @Mock
    Node node;

    @Mock
    NodeHandler<Node> nodeHandler1;

    @Mock
    NodeHandler<Node> nodeHandler2;

    @Mock
    YamlEmitter emitter;

    @Test
    public void handles() throws Exception {

        when(nodeHandler1.handles(node)).thenReturn(true);

        HandlerList list = new HandlerList(Arrays.<NodeHandler<? extends Node>>asList(nodeHandler1, nodeHandler2));

        assertTrue(list.handles(node));
        verify(nodeHandler2, never()).handles(any(Node.class));
    }

    @Test
    public void handlesNone() throws Exception {

        when(nodeHandler1.handles(node)).thenReturn(false);
        when(nodeHandler2.handles(node)).thenReturn(false);

        HandlerList list = new HandlerList(Arrays.<NodeHandler<? extends Node>>asList(nodeHandler1, nodeHandler2));

        assertFalse(list.handles(node));
    }

    @Test
    public void actuallyHandleSafely() throws Exception {

        when(nodeHandler1.handles(node)).thenReturn(false);
        when(nodeHandler2.handles(node)).thenReturn(true);
        when(nodeHandler2.handle(node, emitter)).thenReturn(true);

        HandlerList list = new HandlerList(Arrays.<NodeHandler<? extends Node>>asList(nodeHandler1, nodeHandler2));

        assertTrue(list.handle(node, emitter));
        verify(nodeHandler1, never()).handle(any(Node.class), any(YamlEmitter.class));
        verify(nodeHandler2).handle(node, emitter);
    }

}