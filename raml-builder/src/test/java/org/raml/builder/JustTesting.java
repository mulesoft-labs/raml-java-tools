package org.raml.builder;

import amf.client.model.StrField;
import amf.client.model.domain.AnyShape;
import amf.client.model.domain.DomainElement;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.WebApi;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.raml.builder.RamlDocumentBuilder.document;

/**
 * Created. There, you have it.
 */
public class JustTesting {

    // Example of parsing RAML 1.0 file
    public static void main2(String[] args) throws InterruptedException, ExecutionException {
        // Parse the file
        final WebApiDocument result = (WebApiDocument) Raml10.parse(JustTesting.class.getResource("/org/raml/ramltopojo/object/inherited-type.raml").toString()).get();

        // Log parsed model API
        List<DomainElement> shapes = result.declares();
        WebApi api = (WebApi) result.encodes();
        NodeShape nodeShape = ((NodeShape)api.endPoints().get(0).operations().get(0).responses().get(0).payloads().get(0).schema());
        List<StrField> nodes = api.schemes();
        System.out.println("Parsed Raml10 file. Expected unit encoding webapi: " + api);

    }

    // Example of parsing RAML 1.0 file
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        RamlDocumentBuilder api = document()
                .title("doc")
                .withTypes(() -> {
                    DeclaredShapeBuilder<AnyShape> funk = DeclaredShapeBuilder.typeDeclaration("Funk").ofType(
                            TypeShapeBuilder.inheritingObjectFromShapes()
                                    .withProperty(PropertyShapeBuilder.property("fun", TypeShapeBuilder.stringScalar()))
                    );

                    DeclaredShapeBuilder<AnyShape> funk2 = DeclaredShapeBuilder.typeDeclaration("Funk2").ofType(
                            TypeShapeBuilder.inheritingObjectFromShapes()
                                    .withProperty(PropertyShapeBuilder.property("lala", TypeShapeBuilder.stringScalar()))
                    );

                    DeclaredShapeBuilder<AnyShape> newFunk = DeclaredShapeBuilder.typeDeclaration("Owner").ofType(
                            TypeShapeBuilder.inheritingObjectFromShapes(funk2.asTypeShapeBuilder())
                                    .withProperty(PropertyShapeBuilder.property("owned", funk.asTypeShapeBuilder()))
                    );
                    return Arrays.asList(funk, funk2, newFunk);
                });


        WebApiDocument doc = api.buildModel();

        // Generate RAML 1.0 string from updated model and log it
        String output = Raml10.generateString(doc).get();
        System.out.println("Generated Raml10 string:\n" + output);
    }
}
