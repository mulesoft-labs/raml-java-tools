package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created. There, you have it.
 */
public class NullNodeHandlerTest extends UnitTest {

    @Mock
    private Node notNullNode;

    @Mock
    private NullNode nullNode;

    @Mock
    private YamlEmitter emitter;

    @Test
    public void handles() throws Exception {

        NullNodeHandler handler = new NullNodeHandler();
        assertFalse(handler.handles(notNullNode));
        assertTrue(handler.handles(nullNode));
    }

    @Test
    public void handleSafely() throws Exception {

        NullNodeHandler nul = new NullNodeHandler();
        nul.handleSafely(nullNode, emitter);

        verifyNoMoreInteractions(emitter);
    }

}