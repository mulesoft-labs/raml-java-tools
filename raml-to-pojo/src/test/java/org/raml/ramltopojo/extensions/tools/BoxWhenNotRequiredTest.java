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
public class BoxWhenNotRequiredTest extends UnitTest {

    @Mock
    private IntegerTypeDeclaration integerDeclaration;

    @Test
    public void boxOnNotRequired() {

        when(integerDeclaration.required()).thenReturn(false);

        BoxWhenNotRequired boxWhenNotRequired = new BoxWhenNotRequired(null);
        TypeName tn = boxWhenNotRequired.typeName(null, integerDeclaration, TypeName.INT);
        assertEquals(TypeName.INT.box(), tn);
    }

    @Test
    public void noBoxOnRequired() {

        when(integerDeclaration.required()).thenReturn(true);

        BoxWhenNotRequired boxWhenNotRequired = new BoxWhenNotRequired(null);
        TypeName tn = boxWhenNotRequired.typeName(null, integerDeclaration, TypeName.INT);
        assertEquals(TypeName.INT, tn);
    }

}