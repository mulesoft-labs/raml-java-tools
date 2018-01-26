package org.raml.ramltopojo.extensions.tools;

import com.squareup.javapoet.TypeName;
import org.junit.Test;
import org.mockito.Mock;
import org.raml.testutils.UnitTest;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created. There, you have it.
 */
public class BoxNotRequiredTest extends UnitTest {

    @Mock
    private IntegerTypeDeclaration integerDeclaration;

    @Test
    public void boxOnNotRequired() {

        when(integerDeclaration.required()).thenReturn(false);

        BoxNotRequired boxNotRequired = new BoxNotRequired(null);
        TypeName tn = boxNotRequired.typeName(null, integerDeclaration, TypeName.INT);
        assertEquals(TypeName.INT.box(), tn);
    }

    @Test
    public void noBoxOnRequired() {

        when(integerDeclaration.required()).thenReturn(true);

        BoxNotRequired boxNotRequired = new BoxNotRequired(null);
        TypeName tn = boxNotRequired.typeName(null, integerDeclaration, TypeName.INT);
        assertEquals(TypeName.INT, tn);
    }

}