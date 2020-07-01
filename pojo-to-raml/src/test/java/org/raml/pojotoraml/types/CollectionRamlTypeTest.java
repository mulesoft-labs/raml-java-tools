package org.raml.pojotoraml.types;

import org.junit.Test;
import org.raml.pojotoraml.Fun;

import static org.junit.Assert.*;

/**
 * Created. There, you have it.
 */
public class CollectionRamlTypeTest {
    @Test
    public void wrappingScalar() throws Exception {

        CollectionRamlType collectionRamlType = CollectionRamlType.of(ScalarType.fromType(boolean.class).get());

        assertTrue(collectionRamlType.isScalar());
        assertEquals(boolean.class, collectionRamlType.type());
        assertEquals("array", collectionRamlType.getRamlSyntax(null).id());
    }

    @Test
    public void wrappingComposed() throws Exception {

        CollectionRamlType collectionRamlType = CollectionRamlType.of(ComposedRamlType.forClass(Fun.class, "myName"));

        assertFalse(collectionRamlType.isScalar());
        assertEquals(Fun.class, collectionRamlType.type());
        assertEquals("array", collectionRamlType.getRamlSyntax(null).id());
    }

}