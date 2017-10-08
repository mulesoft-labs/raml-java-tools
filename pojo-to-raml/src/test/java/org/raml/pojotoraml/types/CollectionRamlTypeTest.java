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

        CollectionRamlType collectionRamlType = CollectionRamlType.of(ScalarType.BOOLEAN);

        assertTrue(collectionRamlType.isScalar());
        assertEquals(boolean.class, collectionRamlType.type());
        assertEquals("boolean[]", collectionRamlType.getRamlSyntax());
    }

    @Test
    public void wrappingComposed() throws Exception {

        CollectionRamlType collectionRamlType = CollectionRamlType.of(ComposedRamlType.forClass(Fun.class, "myName"));

        assertFalse(collectionRamlType.isScalar());
        assertEquals(Fun.class, collectionRamlType.type());
        assertEquals("myName[]", collectionRamlType.getRamlSyntax());
    }

}