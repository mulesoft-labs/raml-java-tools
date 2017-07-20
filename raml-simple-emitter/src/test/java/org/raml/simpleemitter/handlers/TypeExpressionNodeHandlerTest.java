package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryRefNode;
import org.raml.yagi.framework.nodes.Node;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class TypeExpressionNodeHandlerTest extends UnitTest {

    @Mock
    private Node badNode;

    @Mock
    private TypeExpressionNode goodNode;

    @Mock
    private YamlEmitter emitter;

    @Mock
    private HandlerList list;

    @Mock
    private LibraryRefNode library;

    @Test
    public void handles() throws Exception {

        TypeExpressionNodeHandler handler = new TypeExpressionNodeHandler(list);
        assertFalse(handler.handles(badNode));
        assertTrue(handler.handles(goodNode));
    }

    @Test
    public void handleSafely() throws Exception {

        when(goodNode.getTypeExpressionText()).thenReturn("JustReference");

        TypeExpressionNodeHandler handler = new TypeExpressionNodeHandler(list);
        handler.handle(goodNode, emitter);

        verify(emitter).writeObjectValue("JustReference");
    }

    @Test
    public void handleSafelyFromLibrary() throws Exception {

        when(goodNode.getTypeExpressionText()).thenReturn("JustReference");
        when(goodNode.findDescendantsWith(LibraryRefNode.class)).thenReturn(Collections.singletonList(library));
        when(library.getRefName()).thenReturn("Library");

        TypeExpressionNodeHandler handler = new TypeExpressionNodeHandler(list);
        handler.handle(goodNode, emitter);

        verify(emitter).writeObjectValue("Library.JustReference");
    }

}