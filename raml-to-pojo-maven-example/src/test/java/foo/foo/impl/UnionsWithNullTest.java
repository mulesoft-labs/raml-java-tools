package foo.foo.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import foo.foo.NilUnionType;
import foo.foo.NilUnionTypeImpl;
import foo.foo.ComplexUnionImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created. There, you have it.
 */
public class UnionsWithNullTest {

    @Test
    public void withString() throws IOException {

        NilUnionTypeImpl nilUnionType = new NilUnionTypeImpl("somevalue");
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, nilUnionType);

        NilUnionType b = mapper.readValue(new StringReader(out.toString()), NilUnionType.class);

        assertEquals("somevalue", b.getString());
    }

    @Test
    public void withNull() throws IOException {

        NilUnionTypeImpl nilUnionType = new NilUnionTypeImpl(null);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, nilUnionType);

        NilUnionType b = mapper.readValue(new StringReader(out.toString()), NilUnionType.class);

        assertTrue(b.isNil());
    }

    @Test
    public void withJsonNodeNull() throws IOException {

        JsonNode node = NullNode.getInstance();
        ObjectMapper mapper = new ObjectMapper();

        NilUnionType b = mapper.treeToValue(node, NilUnionType.class);

        assertTrue(b.isNil());
    }

    @Test
    public void withDifferentTypes() {
        assertTrue(new ComplexUnionImpl(null).isNil());
        assertTrue(new ComplexUnionImpl(true).isBoolean());
        assertTrue(new ComplexUnionImpl("test").isString());
        assertTrue(new ComplexUnionImpl(124).isInteger());
    }

    @Test(expected = IllegalArgumentException.class)
    public void withWrongDifferentTypes() {
        assertTrue(new ComplexUnionImpl(15L).isNil());
    }

}
