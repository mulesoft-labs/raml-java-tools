package foo.foo.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.AddPropsVerificationImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created. There, you have it.
 */
public class AddPropsVerificationImplTest {

    @Test
    public void simpleSuccessTest() throws Exception  {

        ObjectMapper mapper = new ObjectMapper();
        AddPropsVerificationImpl impl = mapper.readValue("{'name': 'dave', 'note1': 'hello'}".replace("'", "\""), AddPropsVerificationImpl.class);

        assertEquals("dave", impl.getName());
        assertEquals("hello", impl.getAdditionalProperties().get("note1"));
    }

    @Test
    public void simpleFailTest() throws Exception  {

        ObjectMapper mapper = new ObjectMapper();
        try {

            AddPropsVerificationImpl impl = mapper.readValue("{'name': 'dave', 'nothing': 'hello'}".replace("'", "\""), AddPropsVerificationImpl.class);
            fail("should fail, bad property");
        } catch (JsonMappingException e) {

            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
        }

    }

}
