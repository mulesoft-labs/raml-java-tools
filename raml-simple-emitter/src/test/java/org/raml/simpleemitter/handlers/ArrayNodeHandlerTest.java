package org.raml.simpleemitter.handlers;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.simpleemitter.HandlerList;
import org.raml.simpleemitter.YamlEmitter;
import org.raml.testutils.UnitTest;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.yagi.framework.nodes.*;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created. There, you have it.
 */
public class ArrayNodeHandlerTest extends UnitTest {

    @Mock
    private Node badNode;

    @Mock
    private ArrayNode goodNode;

    @Mock
    private YamlEmitter emitter;

    @Mock
    private YamlEmitter subEmitter;

    @Mock
    private YamlEmitter bullet;

    @Mock
    private HandlerList list;

    @Mock
    private KeyValueNode object1;

    @Mock
    private ObjectNode object2;

    private StringNodeImpl scalar1 = new StringNodeImpl("one");
    private StringNodeImpl scalar2 = new StringNodeImpl("two");


    @Test
    public void handles() throws Exception {

        ArrayNodeHandler handler = new ArrayNodeHandler(list);
        assertFalse(handler.handles(badNode));
        assertTrue(handler.handles(goodNode));
    }

    @Test
    public void handleSafelyEmpty() throws Exception {

        ArrayNodeHandler handler = new ArrayNodeHandler(list);
        handler.handleSafely(goodNode, emitter);

        verify(emitter).writeSyntaxElement("[");
        verify(emitter).writeSyntaxElement("]");

        verifyNoMoreInteractions(emitter);
    }

    @Test
    public void handleSafelyTwoScalarElements() throws Exception {

        when(goodNode.getChildren()).thenReturn(Arrays.<Node>asList(scalar1, scalar2));
        ArrayNodeHandler handler = new ArrayNodeHandler(list);
        handler.handleSafely(goodNode, emitter);

        verify(emitter).writeSyntaxElement("[");
        verify(emitter).writeSyntaxElement(",");
        verify(emitter).writeSyntaxElement("]");
        verify(list).handle(scalar1, emitter);
        verify(list).handle(scalar2, emitter);
        verifyNoMoreInteractions(emitter, list);
    }

    @Test
    public void handleSafelyTwoNonScalarElements() throws Exception {

        when(goodNode.getChildren()).thenReturn(Arrays.asList(object1, object2));
        when(emitter.indent()).thenReturn(subEmitter);
        when(subEmitter.bulletListArray()).thenReturn(bullet);

        ArrayNodeHandler handler = new ArrayNodeHandler(list);
        handler.handleSafely(goodNode, emitter);

        verify(emitter).indent();

        verify(subEmitter, times(2)).bulletListArray();
        verify(subEmitter, times(2)).writeSyntaxElement("\n");
        verify(subEmitter, times(2)).writeSyntaxElement("- ");
        verify(subEmitter, times(2)).writeIndent();

        verify(list).handle(object1, bullet);
        verify(list).handle(object2, bullet);
        verifyNoMoreInteractions(emitter, subEmitter, list);
    }

}