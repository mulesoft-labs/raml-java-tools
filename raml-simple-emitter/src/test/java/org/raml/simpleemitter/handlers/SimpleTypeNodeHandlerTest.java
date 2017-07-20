package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ReferenceNode;
import org.raml.yagi.framework.nodes.StringNode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class SimpleTypeNodeHandlerTest extends UnitTest {

    @Mock
    private Node badNode;

    @Mock
    private StringNode goodNode;

    @Mock
    private YamlEmitter emitter;

    @Test
    public void handles() throws Exception {

        SimpleTypeNodeHandler handler = new SimpleTypeNodeHandler();
        assertFalse(handler.handles(badNode));
        assertTrue(handler.handles(goodNode));
    }

    @Test
    public void handleSafely() throws Exception {

        SimpleTypeNodeHandler rnh = new SimpleTypeNodeHandler();
        rnh.handleSafely(goodNode, emitter);

        verify(emitter).writeValue(goodNode);
    }

}