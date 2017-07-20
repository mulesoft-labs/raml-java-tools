package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class KeyValueNodeHandlerTest extends UnitTest {

    @Mock
    private HandlerList list;

    @Mock
    private Node notKeyNode;

    @Mock
    private KeyValueNode keyNode;

    @Mock
    private YamlEmitter emitter;

    @Mock
    private YamlEmitter subEmitter;

    @Mock
    private ObjectNode objectNode;

    private SimpleTypeNode<String> simpleNode = new StringNodeImpl("value");


    @Test
    public void handles() throws Exception {

        KeyValueNodeHandler handler = new KeyValueNodeHandler(list);
        assertFalse(handler.handles(notKeyNode));
        assertTrue(handler.handles(keyNode));
    }

    @Test
    public void handleSafelyScalar() throws Exception {

        when(keyNode.getValue()).thenReturn(simpleNode);
        when(keyNode.getKey()).thenReturn(new StringNodeImpl("hello"));
        KeyValueNodeHandler handler = new KeyValueNodeHandler(list);

        assertTrue(handler.handleSafely(keyNode, emitter));
        verify(emitter).writeTag("hello");
        verify(list).handle(simpleNode, emitter);
    }

    @Test
    public void handleSafelyObject() throws Exception {

        when(keyNode.getValue()).thenReturn(objectNode);
        when(emitter.indent()).thenReturn(subEmitter);
        when(keyNode.getKey()).thenReturn(new StringNodeImpl("hello"));

        KeyValueNodeHandler handler = new KeyValueNodeHandler(list);

        assertTrue(handler.handleSafely(keyNode, emitter));
        verify(emitter).writeTag("hello");
        verify(list).handle(objectNode, subEmitter);
    }

}