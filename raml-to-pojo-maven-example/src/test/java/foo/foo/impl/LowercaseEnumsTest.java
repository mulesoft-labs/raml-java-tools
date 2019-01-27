package foo.foo.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.Elevator;
import foo.foo.ElevatorImpl;
import foo.foo.Nametypes;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class LowercaseEnumsTest {


    @Test
    public void doUndo() throws Exception {

        ElevatorImpl elevator = new ElevatorImpl();
        elevator.setName("borneo");
        elevator.setDirection(Nametypes.DOWN);

        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, elevator);

        System.err.println(out.toString());

        Elevator b = mapper.readValue(new StringReader(out.toString()), Elevator.class);

        assertEquals(Nametypes.DOWN, b.getDirection());
        assertEquals("borneo", b.getName());
    }
}
