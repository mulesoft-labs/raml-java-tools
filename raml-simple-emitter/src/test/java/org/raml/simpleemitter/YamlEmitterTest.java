package org.raml.simpleemitter;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class YamlEmitterTest extends UnitTest {
    private Writer writer = new StringWriter();

    @Mock
    private StringNode stringNode;

    @Test
    public void writeValue() throws Exception {

        when(stringNode.getLiteralValue()).thenReturn("hello");
        YamlEmitter emitter = new YamlEmitter(writer, 0);
        emitter.writeValue(stringNode);

        assertEquals("\"hello\"", writer.toString());
    }

    @Test
    public void writeMultilineValue() throws Exception {

        when(stringNode.getLiteralValue()).thenReturn("hello\nman");
        YamlEmitter emitter = new YamlEmitter(writer, 0);
        emitter.writeValue(stringNode);

        assertEquals("|\n    hello\n    man", writer.toString());
    }

}