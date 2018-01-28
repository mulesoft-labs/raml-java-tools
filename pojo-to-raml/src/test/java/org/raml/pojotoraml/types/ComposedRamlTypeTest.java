package org.raml.pojotoraml.types;

import org.junit.Test;
import org.raml.pojotoraml.Fun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created. There, you have it.
 */
public class ComposedRamlTypeTest {

    @Test
    public void simpleTest() {

        ComposedRamlType type = ComposedRamlType.forClass(Fun.class, "real");
        assertEquals("real", type.getRamlSyntax().id());
        assertFalse(type.isScalar());
        assertEquals(Fun.class, type.type());
    }

}