package foo.foo.impl;

import foo.foo.ComplexUnionJ1Impl;
import foo.foo.NilUnionTypeJ1;
import foo.foo.NilUnionTypeJ1Impl;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.NullNode;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created. There, you have it.
 */
public class UnionsWithNullJacksonOneTest {

    @Test
    public void withString() throws IOException {

        NilUnionTypeJ1Impl nilUnionType = new NilUnionTypeJ1Impl("somevalue");
        ObjectMapper mapper = new ObjectMapper();

        StringWriter out = new StringWriter();

        mapper.writeValue(out, nilUnionType);

        NilUnionTypeJ1 b = mapper.readValue(new StringReader(out.toString()), NilUnionTypeJ1.class);

        assertEquals("somevalue", b.getString());
    }

    @Test
    public void withNull() throws IOException {

        NilUnionTypeJ1Impl nilUnionType = new NilUnionTypeJ1Impl(null);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, nilUnionType);

        NilUnionTypeJ1 b = mapper.readValue(new StringReader(out.toString()), NilUnionTypeJ1.class);

        assertTrue(b.isNil());
    }

    @Test
    public void withJsonNodeNull() throws IOException {

        JsonNode node = NullNode.getInstance();
        ObjectMapper mapper = new ObjectMapper();

        NilUnionTypeJ1 b = mapper.treeToValue(node, NilUnionTypeJ1.class);

        assertTrue(b.isNil());
    }

    @Test
    public void withDifferentTypes() {
        assertTrue(new ComplexUnionJ1Impl(null).isNil());
        assertTrue(new ComplexUnionJ1Impl(true).isBoolean());
        assertTrue(new ComplexUnionJ1Impl("test").isString());
        assertTrue(new ComplexUnionJ1Impl(124).isInteger());
    }

    @Test(expected = IllegalArgumentException.class)
    public void withWrongDifferentTypes() {
        assertTrue(new ComplexUnionJ1Impl(15L).isNil());
    }

}
