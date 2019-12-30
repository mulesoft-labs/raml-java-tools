package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.ScalarShape;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created. There, you have it.
 */
public class AmfInheritanceBridgeTest {


    @Test
    public void scalar_inheritance() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                "    mytype:\n" +
                "       type: string\n" +
                "       minLength: 2"
        );

        ScalarShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.inherits()).hasSize(0);
    }


    @Test
    public void scalar_inheritance_two_level() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument("types:\n" +
                "    sup:\n" +
                "       type: string\n" +
                "       maxLength: 6\n" +
                "    mytype:\n" +
                "       type: sup\n" +
                "       minLength: 2");

        ScalarShape shape = (AmfParsingFunctions.findDeclarationByName(doc));
        assertThat(shape.inherits()).hasSize(0);
        assertThat(shape.maxLength().value()).isEqualTo(6);
        assertThat(shape.minLength().value()).isEqualTo(2);
    }

    @Test
    public void scalar_inheritance_two_level_resolve() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument("types:\n" +
                "    sup:\n" +
                "       type: string\n" +
                "       maxLength: 6\n" +
                "    mytype:\n" +
                "       type: sup\n" +
                "       minLength: 2");

        ScalarShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.inherits()).hasSize(0);
        assertThat(shape.maxLength().value()).isEqualTo(6);
        assertThat(shape.minLength().value()).isEqualTo(2);
    }

    @Test
    public void empty_inheritance() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument("types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    mytype:\n" +
                "       type: inherited\n");

        NodeShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.inherits()).hasSize(0);

        assertThat(shape.properties()).hasSize(1).extracting(x -> x.name().value()).contains("age");
    }

    @Test
    public void inheritance_with_square_brackets() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    mytype:\n" +
                "       type: [inherited]");

        NodeShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.inherits()).hasSize(0);
    }

    @Test
    public void inheritance_with_square_brackets_and_property() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    mytype:\n" +
                "       type: [inherited]\n" +
                "       properties:\n" +
                "          name: string\n"
        );

        NodeShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.inherits()).hasSize(0);
    }

    @Test
    public void inheritance_with_props() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    mytype:\n" +
                "       type: inherited\n" +
                "       properties:\n" +
                "          name: string\n"
        );

        NodeShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.properties()).hasSize(2).extracting(x -> x.name().value()).contains("age", "name");
        assertThat(shape.inherits()).hasSize(0);
    }

    @Test
    public void multiple_inheritance_with_props() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    another_inherited:\n" +
                "      properties:\n" +
                "        color: string\n" +
                "    mytype:\n" +
                "       type: [inherited, another_inherited]\n" +
                "       properties:\n" +
                "          name: string\n"
        );

        NodeShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.properties()).hasSize(3).extracting(x -> x.name().value()).contains("age", "color", "name");
        assertThat(shape.inherits()).hasSize(0);
    }

    @Test
    public void multiple_inheritance() throws Exception {

        Document doc = AmfParsingFunctions.resolveDocument(
                "types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    another_inherited:\n" +
                "      properties:\n" +
                "        color: string\n" +
                "    mytype:\n" +
                "       type: [inherited, another_inherited]\n"
        );

        NodeShape shape = AmfParsingFunctions.findDeclarationByName(doc);
        assertThat(shape.properties()).hasSize(2).extracting(f -> f.name().value()).contains("age", "color");
        assertThat(shape.inherits()).hasSize(0);
    }


}
