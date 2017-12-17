package org.raml.ramltopojo;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Arrays;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class GenerationContextImplTest extends UnitTest{

    @Mock
    ObjectTypeDeclaration type1, type2, type3, type4;

    @Test
    public void setupTypeHierarchy() {

        when(type1.parentTypes()).thenReturn(Arrays.<TypeDeclaration>asList(type2, type3));
        when(type2.parentTypes()).thenReturn(Arrays.<TypeDeclaration>asList(type3, type4));
        when(type1.name()).thenReturn("type1");
        when(type2.name()).thenReturn("type2");
        when(type3.name()).thenReturn("type3");
        when(type4.name()).thenReturn("type4");

        GenerationContextImpl impl = new GenerationContextImpl(null);
        impl.setupTypeHierarchy(type1);

        assertThat(impl.childClasses("type2"), Matchers.contains(Matchers.equalTo("type1")));

        assertThat(impl.childClasses("type3"), Matchers.contains(Matchers.equalTo("type1"), Matchers.equalTo("type2")));
    }
}