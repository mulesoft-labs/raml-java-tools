package foo.foo.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.Cat;
import foo.foo.CatImpl;
import foo.foo.PetResponse;
import foo.foo.PetResponseImpl;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class AnimalsTest {


    @Test
    public void doUndo() throws Exception {

        PetResponse response = new PetResponseImpl();
        CatImpl cat = new CatImpl();
        cat.setLegs(4);

        response.setMyPet(cat);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();
        mapper.writeValue(out, response);

        System.err.println(out.toString());

        PetResponse b = mapper.readValue(new StringReader(out.toString()), PetResponse.class);

        assertEquals(4, ((Cat)b.getMyPet()).getLegs());
    }
}
