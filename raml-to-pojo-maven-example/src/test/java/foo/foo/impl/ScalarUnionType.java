package foo.foo.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.SomeScalar;
import foo.foo.SomeScalarImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class ScalarUnionType {

    @Test
    public void withString() throws IOException {

        SomeScalarImpl someScalar = new SomeScalarImpl("somevalue");
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, someScalar);

        SomeScalar b = mapper.readValue(new StringReader(out.toString()), SomeScalar.class);

        assertEquals("somevalue", b.getString());
    }

    @Test
    public void withNull() throws IOException {

        SomeScalarImpl someScalar = new SomeScalarImpl(123);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, someScalar);

        SomeScalar b = mapper.readValue(new StringReader(out.toString()), SomeScalar.class);

        assertEquals(new Integer(123), b.getInteger());
    }
}
