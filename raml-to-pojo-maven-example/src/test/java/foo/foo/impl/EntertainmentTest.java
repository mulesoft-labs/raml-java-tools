package foo.foo.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.EntertainementImpl;
import foo.foo.ProjectorImpl;
import foo.foo.TelevisionImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * Created. There, you have it.
 */
public class EntertainmentTest {

    @Test
    public void withTelevision() throws IOException {

        TelevisionImpl ti = new TelevisionImpl();
        ti.setColor(true);
        EntertainementImpl entertainement = new EntertainementImpl(ti);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, entertainement);

        EntertainementImpl b = mapper.readValue(new StringReader(out.toString()), EntertainementImpl.class);

        assertTrue(b.isTelevision());
    }

    @Test
    public void withNull() throws IOException {

        ProjectorImpl ti = new ProjectorImpl();
        ti.setHanging(true);
        EntertainementImpl entertainement = new EntertainementImpl(ti);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, entertainement);

        EntertainementImpl b = mapper.readValue(new StringReader(out.toString()), EntertainementImpl.class);

        assertTrue(b.isProjector());
    }

}
