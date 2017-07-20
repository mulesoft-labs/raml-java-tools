package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.Node;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created. There, you have it.
 */
public class DefaultNodeHandlerTest extends UnitTest {

    @Mock
    private Node node;

    @Mock
    private YamlEmitter emitter;

    @Test
    public void handles() throws Exception {

        DefaultNodeHandler handler = new DefaultNodeHandler();
        assertTrue(handler.handles(node));
    }

    @Test
    public void handleSafely() throws Exception {

        DefaultNodeHandler handler = new DefaultNodeHandler();
        assertTrue(handler.handle(node, emitter));

        verifyNoMoreInteractions(emitter);
    }

}