package org.raml.pojotoraml.types;

import org.junit.Test;
import org.mockito.Mock;
import org.raml.pojotoraml.AdjusterFactory;
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.Fun;
import org.raml.pojotoraml.RamlAdjuster;
import org.raml.pojotoraml.field.SubFun;
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
    AdjusterFactory adjusterFactory;

    @Mock
    RamlAdjuster adjuster;

    @Test
    public void forScalars() throws Exception {

        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("one").getGenericType(), null, null).orElse(null);
        assertEquals(null, type.getRamlSyntax(null).name());
    }

    @Test
    public void forComposed() throws Exception {

        when(adjusterFactory.createAdjuster(SubFun.class)).thenReturn(adjuster);
        when(adjuster.adjustTypeName(SubFun.class, "SubFun")).thenReturn("foo");
        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("sub").getGenericType(), classParser, adjusterFactory).orElse(null);
        assertTrue(type instanceof ComposedRamlType);
        assertEquals("foo", type.getRamlSyntax(null).name());
    }

    @Test
    public void forCollectionsOfScalars() throws Exception {

        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("listOfStrings").getGenericType(), null, null).orElse(null);
        assertTrue(type instanceof CollectionRamlType);
        assertEquals("array", type.getRamlSyntax(null).name());
    }

    @Test
    public void forCollectionsOfScalarsFromArray() throws Exception {

        when(adjusterFactory.createAdjuster(Fun.class)).thenReturn(RamlAdjuster.NULL_ADJUSTER);

        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("arrayOfInts").getGenericType(), null, adjusterFactory).orElse(null);
        assertTrue(type instanceof CollectionRamlType);
        assertEquals("array", type.getRamlSyntax(null).name());
    }

    @Test
    public void forListOfComposed() throws Exception {

        when(adjusterFactory.createAdjuster(SubFun.class)).thenReturn(RamlAdjuster.NULL_ADJUSTER);
        when(adjuster.adjustTypeName(SubFun.class, "SubFun")).thenReturn("foo");
        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("listOfSubs").getGenericType(), classParser, adjusterFactory).orElse(null);
        assertTrue(type instanceof CollectionRamlType);
        assertEquals("array", type.getRamlSyntax(null).name());
    }

    @Test
    public void forArrayOfComposed() throws Exception {

        when(adjusterFactory.createAdjuster(SubFun.class)).thenReturn(RamlAdjuster.NULL_ADJUSTER);
        RamlType type = RamlTypeFactory.forType(Fun.class.getDeclaredField("arrayOfSubs").getGenericType(), null, adjusterFactory).orElse(null);
        assertTrue(type instanceof CollectionRamlType);
        assertEquals("array", type.getRamlSyntax(null).name());
    }

}