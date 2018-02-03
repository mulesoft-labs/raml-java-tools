package org.raml.pojotoraml.field;


import org.junit.Test;
import org.raml.pojotoraml.Fun;
import org.raml.pojotoraml.Property;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class FieldClassSourceTest {

    @Test
    public void properties() throws Exception {

        FieldClassParser source = new FieldClassParser();
        List<Property> props =  source.properties(Fun.class);
        assertEquals(7, props.size());
        assertEquals("one", props.get(0).name());
        assertEquals(String.class, props.get(0).type());

        assertEquals("two", props.get(1).name());
        assertEquals(int.class, props.get(1).type());

        assertEquals("sub", props.get(2).name());
        assertEquals(SubFun.class, props.get(2).type());

        assertEquals("listOfStrings", props.get(3).name());
        assertEquals("java.util.List<java.lang.String>", props.get(3).type().toString());

        assertEquals("listOfSubs", props.get(4).name());
        assertEquals("java.util.List<org.raml.pojotoraml.field.SubFun>", props.get(4).type().toString());
    }

}