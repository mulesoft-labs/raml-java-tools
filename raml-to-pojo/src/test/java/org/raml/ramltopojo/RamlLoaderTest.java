package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.UnionShape;
import org.junit.Test;
import org.raml.ramltopojo.amf.ExtraInformationImpl;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created. There, you have it.
 */
public class RamlLoaderTest {

    public static String header() {
        return "#%RAML 1.0\n" +
                "title: Hello World API\n" +
                "version: v1\n" +
                "baseUri: https://api.github.com\n";
    }

    @Test
    public void inline_property() throws ExecutionException, InterruptedException {

        Document doc = RamlLoader.load(
                header() +
                        "types:\n" +
                        "    mytype:\n" +
                        "       type: object\n" +
                        "       properties:\n" +
                        "          goo: string|integer\n" +
                        "/fun:\n" +
                        "   get:\n" +
                        "   /funkytown:\n" +
                        "      get:"
        );

        NodeShape shape = RamlLoader.findShape("mytype", doc.declares());
        assertTrue(ExtraInformationImpl.isInline(shape.properties().get(0).range()));
    }

    @Test
    public void type_in_resource() throws ExecutionException, InterruptedException {

        Document doc = RamlLoader.load(
                header() +
                        "types:\n" +
                        "    mytype:\n" +
                        "       type: object\n" +
                        "       properties:\n" +
                        "          goo: string\n" +
                        "/fun:\n" +
                        "   get:\n" +
                        "      body:\n" +
                        "        application/json: \n" +
                        "            type: mytype\n"

        );

        NodeShape shape = RamlLoader.findShapeInResourceBody("/fun", "get", doc);
        assertTrue(ExtraInformationImpl.isInline(shape));
    }

    @Test
    public void type_in_resource_non_inline() throws ExecutionException, InterruptedException {

        Document doc = RamlLoader.load(
                header() +
                        "types:\n" +
                        "    mytype:\n" +
                        "       type: object\n" +
                        "       properties:\n" +
                        "          goo: string\n" +
                        "/fun:\n" +
                        "   get:\n" +
                        "      body:\n" +
                        "        application/json: mytype\n"

        );

        NodeShape shape = RamlLoader.findShapeInResourceBody("/fun", "get", doc);
        assertFalse(ExtraInformationImpl.isInline(shape));
    }
    @Test
    public void inline_array() throws ExecutionException, InterruptedException {

        Document doc = RamlLoader.load(
                header() +
                        "types:\n" +
                        "    mytype:\n" +
                        "       type: object\n" +
                        "       properties:\n" +
                        "          goo: (string|integer)[]\n"
        );

        NodeShape shape = RamlLoader.findShape("mytype", doc.declares());
        assertTrue(ExtraInformationImpl.isInline(shape.properties().get(0).range()));
    }

    @Test
    public void normal_union() throws ExecutionException, InterruptedException {

        Document doc = RamlLoader.load(
                header() +
                        "types:\n" +
                        "    mytype:\n" +
                        "       type: string|integer\n"
        );

        UnionShape shape = RamlLoader.findShape("mytype", doc.declares());
        assertFalse(ExtraInformationImpl.isInline(shape));
    }

    @Test
    public void parent_classes() throws ExecutionException, InterruptedException {

        Document doc = RamlLoader.load(
                header() +
                        "types:\n" +
                        "    typeOne:\n" +
                        "       type: object\n" +
                        "       properties:\n" +
                        "          goo: string\n" +
                        "    mytype:\n" +
                        "       type: typeOne\n" +
                        "       properties:\n" +
                        "          goo: string\n"
        );

        NodeShape shape = RamlLoader.findShape("mytype", doc.declares());
        assertThat(ExtraInformationImpl.parentTypes(shape)).hasSize(1).containsExactly("http://a.ml/amf/default_document#/declarations/types/typeOne");
    }

}