package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ReferenceNode;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class TypeDeclarationNodeHandlerTest extends UnitTest {

    @Mock
    private Node badNode;

    @Mock
    private TypeDeclarationNode goodNode;

    @Mock
    private YamlEmitter emitter;

    @Mock
    private HandlerList list;

    @Mock
    private KeyValueNode keyValueNode;

    @Test
    public void handles() throws Exception {

        TypeDeclarationNodeHandler tdnh = new TypeDeclarationNodeHandler(list);
        assertFalse(tdnh.handles(badNode));
        assertTrue(tdnh.handles(goodNode));
    }

    @Test
    public void handleSafely() throws Exception {

        when(goodNode.getChildren()).thenReturn(Collections.<Node>singletonList(keyValueNode));
        TypeDeclarationNodeHandler handler = new TypeDeclarationNodeHandler(list);
        handler.handleSafely(goodNode, emitter);

        verify(list).handle(keyValueNode, emitter);
    }

}