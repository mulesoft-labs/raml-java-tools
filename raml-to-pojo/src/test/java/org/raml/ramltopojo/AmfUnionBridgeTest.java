package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.UnionShape;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created. There, you have it.
 */
public class AmfUnionBridgeTest {

    @Test
    public void unionOfScalars() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                        "    mytype:\n" +
                        "       type: string | integer");

        UnionShape shape = AmfParsingFunctions.findDeclarationByName(doc, "mytype");
        assertThat(shape.anyOf()).hasSize(2);
        assertThat(shape.inherits()).hasSize(0);
        assertThat(shape.anyOf())
                .hasSize(2)
                .extracting(x -> ((ScalarShape)x).dataType().value()).contains(ScalarTypes.STRING_SCALAR, ScalarTypes.INTEGER_SCALAR);
    }

    @Test
    public void unionOfTypes() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                        "    type1:\n" +
                        "      properties:\n" +
                        "          name: string\n" +
                        "    type2:\n" +
                        "      properties:\n" +
                        "          age: integer\n" +
                        "    mytype:\n" +
                        "       type: type1 | type2");

        UnionShape shape = AmfParsingFunctions.findDeclarationByName(doc, "mytype");
        assertThat(shape.anyOf()).hasSize(2);
        assertThat(shape.inherits()).hasSize(0);
        assertThat(shape.anyOf())
                .hasSize(2)
                .extracting(x -> ((NodeShape)x).name().value()).contains("type1", "type2");
    }

    @Test
    public void unionOfTypesWithProperties() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                        "    type1:\n" +
                        "      properties:\n" +
                        "          name: string\n" +
                        "    type2:\n" +
                        "      properties:\n" +
                        "          age: integer\n" +
                        "    mytype:\n" +
                        "       type: type1 | type2\n" +
                        "       properties:\n" +
                        "           email: string");

        UnionShape shape = AmfParsingFunctions.findDeclarationByName(doc, "mytype");
        assertThat(shape.anyOf()).hasSize(2);
        assertThat(shape.inherits()).hasSize(0);
        assertThat(shape.customShapeProperties()).hasSize(1);
        assertThat(shape.anyOf())
                .hasSize(2)
                .extracting(x -> ((NodeShape)x).name().value()).contains("type1", "type2");
    }
}
