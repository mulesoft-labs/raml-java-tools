package org.raml.pojotoraml.types;

import amf.client.model.domain.ArrayShape;
import amf.core.parser.ArrayNode;
import org.junit.Test;
import org.raml.builder.TypeShapeBuilder;
import org.raml.pojotoraml.Fun;
import org.raml.ramltopojo.RamlToPojoImpl;

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
        assertTrue( collectionRamlType.getRamlSyntax((t) -> TypeShapeBuilder.booleanScalar() ).asTypeShapeBuilder().buildNode() instanceof ArrayShape);
    }

    @Test
    public void wrappingComposed() throws Exception {

        CollectionRamlType collectionRamlType = CollectionRamlType.of(ComposedRamlType.forClass(Fun.class, "myName"));

        assertFalse(collectionRamlType.isScalar());
        assertEquals(Fun.class, collectionRamlType.type());
        assertTrue( collectionRamlType.getRamlSyntax((t) -> TypeShapeBuilder.inheritingObjectFromShapes() ).asTypeShapeBuilder().buildNode() instanceof ArrayShape);
    }

}