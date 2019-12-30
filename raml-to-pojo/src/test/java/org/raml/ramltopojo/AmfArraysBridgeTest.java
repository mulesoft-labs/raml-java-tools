package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.ScalarShape;
import amf.client.model.domain.UnionShape;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created. There, you have it.
 */
public class AmfArraysBridgeTest {


    @Test
    public void arrays() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                        "    mytype:\n" +
                        "       type: string[]");

        ArrayShape shape = AmfParsingFunctions.findDeclarationByName(doc, "mytype");
        assertThat((ScalarShape)shape.items()).isNotNull().extracting(i -> i.dataType().value()).isEqualTo(ScalarTypes.STRING_SCALAR);
    }

    @Test
    public void arrays_types() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                        "    another_type:\n" +
                        "          properties:\n" +
                        "            age: integer\n" +
                        "    mytype:\n" +
                        "       type: another_type[]");

        ArrayShape shape = AmfParsingFunctions.findDeclarationByName(doc, "mytype");
        assertThat((NodeShape)shape.items()).isNotNull().extracting(i -> i.name().value()).isEqualTo("another_type");
    }

    @Test
    public void arrays_lets_be_explicit() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                        "    another_type:\n" +
                        "          properties:\n" +
                        "            age: integer\n" +
                        "    mytype:\n" +
                        "       type: array\n" +
                        "       items: another_type");

        ArrayShape shape = AmfParsingFunctions.findDeclarationByName(doc, "mytype");
        assertThat((NodeShape)shape.items()).isNotNull().extracting(i -> i.name().value()).isEqualTo("another_type");
    }

    @Test
    public void arrays_lets_be_explicit_and_inline() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                        "    another_type:\n" +
                        "          properties:\n" +
                        "            age: integer\n" +
                        "    mytype:\n" +
                        "       type: array\n" +
                        "       items: another_type|string");

        ArrayShape shape = AmfParsingFunctions.findDeclarationByName(doc, "mytype");
        assertThat((UnionShape)shape.items()).isNotNull().isNot(AmfParsingFunctions.IS_INLINE);
    }


}
