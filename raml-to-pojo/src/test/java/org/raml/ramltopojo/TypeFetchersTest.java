package org.raml.ramltopojo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class TypeFetchersTest extends UnitTest{

    @Mock
    Api api;

    @Mock
    TypeDeclaration t1, t2, t3, t4, t5, t6;

    @Mock
    Library l1, l2, l3;

    @Before
    public void before() {

        when(l1.name()).thenReturn("l1");
        when(l2.name()).thenReturn("l2");
        when(l3.name()).thenReturn("l3");

        when(t1.name()).thenReturn("t1");
        when(t2.name()).thenReturn("t2");
        when(t3.name()).thenReturn("t3");
        when(t4.name()).thenReturn("t4");
        when(t5.name()).thenReturn("t5");
        when(t6.name()).thenReturn("t6");
    }

    @Test
    public void fromTypes() throws Exception {

        when(api.types()).thenReturn(Arrays.asList(t1, t2, t3));

        TypeDeclaration typeDeclaration = TypeFetchers.fromTypes().fetchType(api, "t1");

        assertSame(t1, typeDeclaration);
    }

    @Test(expected = GenerationException.class)
    public void fromTypesFail() throws Exception {

        when(api.types()).thenReturn(Arrays.asList(t4, t5, t6));

        when(api.uses()).thenReturn(Arrays.asList(l1, l2));
        when(l1.uses()).thenReturn(Collections.singletonList(l3));

        when(l1.types()).thenReturn(Collections.singletonList(t1));
        when(l2.types()).thenReturn(Collections.singletonList(t2));
        when(l3.types()).thenReturn(Collections.singletonList(t3));

        TypeFetchers.fromTypes().fetchType(api, "nosuchtype");
    }

    @Test
    public void fromLibraries() throws Exception {

        when(api.uses()).thenReturn(Arrays.asList(l1, l2));
        when(l1.uses()).thenReturn(Collections.singletonList(l3));

        when(l1.types()).thenReturn(Collections.singletonList(t1));
        when(l2.types()).thenReturn(Collections.singletonList(t2));
        when(l3.types()).thenReturn(Collections.singletonList(t3));

        TypeDeclaration typeDeclaration1 = TypeFetchers.fromLibraries().fetchType(api, "t1");
        TypeDeclaration typeDeclaration2 = TypeFetchers.fromLibraries().fetchType(api, "t2");
        TypeDeclaration typeDeclaration3 = TypeFetchers.fromLibraries().fetchType(api, "t3");

        assertSame(typeDeclaration1, t1);
        assertSame(typeDeclaration2, t2);
        assertSame(typeDeclaration3, t3);

    }

    @Test
    public void fromAnywhere() throws Exception {

        when(api.types()).thenReturn(Arrays.asList(t4, t5, t6));

        when(api.uses()).thenReturn(Arrays.asList(l1, l2));
        when(l1.uses()).thenReturn(Collections.singletonList(l3));

        when(l1.types()).thenReturn(Collections.singletonList(t1));
        when(l2.types()).thenReturn(Collections.singletonList(t2));
        when(l3.types()).thenReturn(Collections.singletonList(t3));

        TypeDeclaration typeDeclaration1 = TypeFetchers.fromAnywhere().fetchType(api, "t1");
        TypeDeclaration typeDeclaration2 = TypeFetchers.fromAnywhere().fetchType(api, "t2");
        TypeDeclaration typeDeclaration3 = TypeFetchers.fromAnywhere().fetchType(api, "t3");
        TypeDeclaration typeDeclaration4 = TypeFetchers.fromAnywhere().fetchType(api, "t4");
        TypeDeclaration typeDeclaration5 = TypeFetchers.fromAnywhere().fetchType(api, "t5");
        TypeDeclaration typeDeclaration6 = TypeFetchers.fromAnywhere().fetchType(api, "t6");

        assertSame(typeDeclaration1, t1);
        assertSame(typeDeclaration2, t2);
        assertSame(typeDeclaration3, t3);
        assertSame(typeDeclaration4, t4);
        assertSame(typeDeclaration5, t5);
        assertSame(typeDeclaration6, t6);

    }

    @Test(expected = GenerationException.class)
    public void failFromLibraries() throws Exception {

        when(api.types()).thenReturn(Arrays.asList(t4, t5, t6));

        when(api.uses()).thenReturn(Arrays.asList(l1, l2));
        when(l1.uses()).thenReturn(Collections.singletonList(l3));

        when(l1.types()).thenReturn(Collections.singletonList(t1));
        when(l2.types()).thenReturn(Collections.singletonList(t2));
        when(l3.types()).thenReturn(Collections.singletonList(t3));

        TypeFetchers.fromLibraries().fetchType(api, "t4");

    }

}