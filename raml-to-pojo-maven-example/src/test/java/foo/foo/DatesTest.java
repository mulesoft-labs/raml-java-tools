package foo.foo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class DatesTest {

    static private  Date time ;
    static {
        try {
            time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse("2010-07-22T13:04:55-0000");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void withNoColon() throws Exception {

        ObjectMapper mapper =  new ObjectMapper();
        StringReader reader = new StringReader("{ \"someDate\": \"2010-07-22T09:04:55-0400\"}");
        Dates d = mapper.readValue(reader, Dates.class);
        assertEquals(time.getTime(), d.getSomeDate().getTime());
    }


    @Test
    public void withAnOffset() throws Exception {

        ObjectMapper mapper =  new ObjectMapper();
        StringReader reader = new StringReader("{ \"someDate\": \"2010-07-22T09:04:55-04:00\"}");
        Dates d = mapper.readValue(reader, Dates.class);
        assertEquals(time.getTime(), d.getSomeDate().getTime());
    }

    @Test
    public void withNoColonNL() throws Exception {

        ObjectMapper mapper =  new ObjectMapper();
        StringReader reader = new StringReader("{ \"someDate\": \"2010-07-22T09:34:55-0330\"}");
        Dates d = mapper.readValue(reader, Dates.class);
        assertEquals(time.getTime(), d.getSomeDate().getTime());
    }


    @Test
    public void withAnOffsetNL() throws Exception {

        ObjectMapper mapper =  new ObjectMapper();
        StringReader reader = new StringReader("{ \"someDate\": \"2010-07-22T09:34:55-03:30\"}");
        Dates d = mapper.readValue(reader, Dates.class);
        assertEquals(time, d.getSomeDate());
    }

}
