package foo.foo.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import foo.foo.*;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * Created. There, you have it.
 */
public class BiomesTest {


    @Test
    public void doUndo() throws Exception {

        JungleImpl jungle = new JungleImpl();
        jungle.setName("borneo");
        jungle.setAnimals(new JungleAnimal[] {new JungleAnimalImpl(), new JungleAnimalImpl()});

        BiomeImpl biome = new BiomeImpl(jungle);

        ObjectMapper mapper = new ObjectMapper();
        StringWriter out = new StringWriter();

        mapper.writeValue(out, biome);

        System.err.println(out.toString());

        Biome b = mapper.readValue(new StringReader(out.toString()), Biome.class);

        assertTrue(b.isJungle());
    }
}
