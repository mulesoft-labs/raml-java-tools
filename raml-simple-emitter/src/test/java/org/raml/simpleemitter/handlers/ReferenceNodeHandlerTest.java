package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ReferenceNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class ReferenceNodeHandlerTest extends UnitTest {

    @Mock
    private Node badNode;

    @Mock
    private ReferenceNode goodNode;

    @Mock
    private YamlEmitter emitter;

    @Test
    public void handles() throws Exception {

        ReferenceNodeHandler handler = new ReferenceNodeHandler();
        assertFalse(handler.handles(badNode));
        assertTrue(handler.handles(goodNode));
    }

    @Test
    public void handleSafely() throws Exception {

        when(goodNode.getRefName()).thenReturn("ref.value");
        ReferenceNodeHandler rnh = new ReferenceNodeHandler();
        rnh.handleSafely(goodNode, emitter);

        verify(emitter).writeObjectValue("ref.value");
    }

}