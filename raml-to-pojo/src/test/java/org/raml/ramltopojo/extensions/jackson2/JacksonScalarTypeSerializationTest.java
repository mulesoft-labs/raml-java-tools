package org.raml.ramltopojo.extensions.jackson2;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class JacksonScalarTypeSerializationTest {

    @Mock
    private TypeDeclaration declaration;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void onEnumConstant() throws Exception {

       /* TypeSpec.Builder builder = TypeSpec.classBuilder("Boo");
        JacksonScalarTypeSerialization ser = new JacksonScalarTypeSerialization();
        ser.fieldBuilt(null, declaration, builder, EventType.IMPLEMENTATION);

        TypeSpec spec = builder.build();

        assertThat(spec.annotations.get(0), hasMember("value"));
        assertThat(spec.annotations.get(0), member("value", contains(codeBlockContents(equalTo("\"foo\"")))));*/
    }


}