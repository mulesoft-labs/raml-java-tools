package org.raml.pojotoraml.types;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.Fun;
import org.raml.pojotoraml.RamlAdjuster;
import org.raml.testutils.UnitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class RamlTypeFactoryTest  extends UnitTest {

    @Mock
    ClassParser classParser;

    @Mock
    RamlAdjuster adjuster;

    @Test
    public void forScalars() throws Exception {

        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("one").getGenericType(), null, null);
        assertTrue(type instanceof ScalarType);
    }

    @Test
    public void forComposed() throws Exception {

        when(adjuster.adjustTypeName("SubFun", classParser)).thenReturn("foo");
        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("sub").getGenericType(), classParser, adjuster);
        assertTrue(type instanceof ComposedRamlType);
        assertEquals("foo", type.getRamlSyntax());
    }

    @Test
    public void forCollectionsOfScalars() throws Exception {

        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("listOfStrings").getGenericType(), null, null);
        assertTrue(type instanceof CollectionRamlType);
        assertEquals("string[]", type.getRamlSyntax());
    }

    @Test
    public void forListOfComposed() throws Exception {

        when(adjuster.adjustTypeName("SubFun", classParser)).thenReturn("foo");
        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("listOfSubs").getGenericType(), classParser, adjuster);
        assertTrue(type instanceof CollectionRamlType);
        assertEquals("foo[]", type.getRamlSyntax());
    }

}