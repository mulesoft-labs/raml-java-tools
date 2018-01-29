package org.raml.simpleemitter;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.yagi.framework.nodes.StringNode;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class YamlEmitterTest extends UnitTest {
    private Writer writer = new StringWriter();

    @Mock
    private StringNode stringNode;

    @Test
    public void writeValueClean() throws Exception {

        when(stringNode.getLiteralValue()).thenReturn("hello");
        YamlEmitter emitter = new YamlEmitter(writer, 0);
        emitter.writeValue(stringNode);

        assertEquals("hello", writer.toString());
    }

    @Test
    public void writeValue() throws Exception {

        when(stringNode.getLiteralValue()).thenReturn("hel@lo");
        YamlEmitter emitter = new YamlEmitter(writer, 0);
        emitter.writeValue(stringNode);

        assertEquals("\"hel@lo\"", writer.toString());
    }

    @Test
    public void writeMultilineValue() throws Exception {

        when(stringNode.getLiteralValue()).thenReturn("hello\nman");
        YamlEmitter emitter = new YamlEmitter(writer, 0);
        emitter.writeValue(stringNode);

        assertEquals("|\n    hello\n    man", writer.toString());
    }

    @Test
    public void writeMultilineValueBecauseOfQuote() throws Exception {

        when(stringNode.getLiteralValue()).thenReturn("hello\"man");
        YamlEmitter emitter = new YamlEmitter(writer, 0);
        emitter.writeValue(stringNode);

        assertEquals("|\n    hello\"man", writer.toString());
    }

    @Test
    public void bulletListEmitter() throws Exception {

        YamlEmitter e = new YamlEmitter(writer, 0);
        YamlEmitter bullet = e.bulletListArray();
        bullet.writeTag("foo");
        bullet.writeTag("bar");

        assertEquals("foo: \n  bar: ", writer.toString());
    }

    @Test
    public void bulletListEmitterWithString() throws Exception {

        YamlEmitter e = new YamlEmitter(writer, 0);
        YamlEmitter bullet = e.bulletListArray();
        bullet.writeTag("tag");
        bullet.writeObjectValue("foo\nbar");

        assertEquals("tag: |\n          foo\n          bar", writer.toString());
    }

}