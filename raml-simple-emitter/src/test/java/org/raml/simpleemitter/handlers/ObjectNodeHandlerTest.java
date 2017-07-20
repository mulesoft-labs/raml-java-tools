package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class ObjectNodeHandlerTest extends UnitTest {

    @Mock
    private HandlerList list;

    @Mock
    private YamlEmitter emitter;

    @Mock
    private ObjectNode objectNode;

    @Mock
    private KeyValueNode child;

    private StringNodeImpl scalarNode = new StringNodeImpl("foo");

    @Test
    public void handleSafely() throws Exception {

        when(objectNode.getChildren()).thenReturn(Collections.<Node>singletonList(child));
        ObjectNodeHandler on = new ObjectNodeHandler(list);
        on.handleSafely(objectNode, emitter);

        verify(list).handle(child, emitter);
    }

    // no sure this happens.
    @Test
    public void handleSafelyScalar() throws Exception {

        when(objectNode.getChildren()).thenReturn(Collections.<Node>singletonList(scalarNode));
        ObjectNodeHandler on = new ObjectNodeHandler(list);
        on.handleSafely(objectNode, emitter);

        verify(emitter).writeObjectValue("foo");
    }

}