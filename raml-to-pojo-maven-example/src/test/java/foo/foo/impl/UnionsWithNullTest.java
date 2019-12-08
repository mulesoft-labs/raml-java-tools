package foo.foo.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.NilUnionType;
import foo.foo.NilUnionTypeImpl;
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

    //@Test
    public void withNull() throws IOException {

        NilUnionTypeImpl nilUnionType = new NilUnionTypeImpl();
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, nilUnionType);

        NilUnionType b = mapper.readValue(new StringReader(out.toString()), NilUnionType.class);

        assertTrue(b.isNil());
    }

}
