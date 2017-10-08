package org.raml.pojotoraml.field;


import org.junit.Test;
import org.raml.pojotoraml.Property;

import java.util.List;

import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class FieldClassSourceTest {
    @Test
    public void underlyingClass() throws Exception {

        FieldClassParser source = new FieldClassParser(Fun.class);
        assertSame(Fun.class, source.underlyingClass());
    }

    @Test
    public void properties() throws Exception {

        FieldClassParser source = new FieldClassParser(Fun.class);
        List<Property> props =  source.properties();
        assertEquals(2, props.size());
        assertEquals("one", props.get(0).name());
        assertEquals(String.class, props.get(0).type());

        assertEquals("two", props.get(1).name());
        assertEquals(int.class, props.get(1).type());
    }

}