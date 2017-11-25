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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class TypeFindersTest extends UnitTest{

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
    }
    @Test
    public void inTypes() {
        when(api.types()).thenReturn(Arrays.asList(t1, t2, t3));

        Iterable<TypeDeclaration> it = TypeFinders.inTypes().findTypes(api);

        assertThat(it, contains(equalTo(t1), equalTo(t2), equalTo(t3)));

    }

    @Test
    public void inLibraries() {
        when(api.uses()).thenReturn(Arrays.asList(l1, l2));
        when(l1.uses()).thenReturn(Collections.singletonList(l3));

        when(l1.types()).thenReturn(Collections.singletonList(t1));
        when(l2.types()).thenReturn(Collections.singletonList(t2));
        when(l3.types()).thenReturn(Collections.singletonList(t3));

        Iterable<TypeDeclaration> it = TypeFinders.inLibraries().findTypes(api);

        System.err.println(it);
        assertThat(it, containsInAnyOrder(equalTo(t1), equalTo(t2), equalTo(t3)));

    }

    @Test
    public void everyWhere() {

        when(api.types()).thenReturn(Arrays.asList(t4, t5, t6));

        when(api.uses()).thenReturn(Arrays.asList(l1, l2));
        when(l1.uses()).thenReturn(Collections.singletonList(l3));

        when(l1.types()).thenReturn(Collections.singletonList(t1));
        when(l2.types()).thenReturn(Collections.singletonList(t2));
        when(l3.types()).thenReturn(Collections.singletonList(t3));

        Iterable<TypeDeclaration> it = TypeFinders.everyWhere().findTypes(api);

        System.err.println(it);
        assertThat(it, containsInAnyOrder(equalTo(t1), equalTo(t2), equalTo(t3), equalTo(t4), equalTo(t5), equalTo(t6)));
    }

}