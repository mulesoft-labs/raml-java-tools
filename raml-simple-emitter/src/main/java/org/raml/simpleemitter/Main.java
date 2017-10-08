package org.raml.simpleemitter;


import org.raml.builder.*;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.GrammarPhase;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import static org.raml.builder.MethodBuilder.method;
import static org.raml.builder.NodeBuilders.key;
import static org.raml.builder.NodeBuilders.property;
import static org.raml.builder.RamlDocumentBuilder.document;
import static org.raml.builder.ResourceBuilder.resource;
import static org.raml.builder.ResponseBuilder.response;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created by jpbelang on 2017-06-25.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // URL url = Main.class.getResource("api.raml");
        URL url = Main.class.getResource("fun.raml");

        Reader reader = new InputStreamReader(url.openStream());

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(reader, url.getFile());
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.err.println(validationResult);
            }
        } else {

            Api api = document()
                    .baseUri("http:fun.com/fun")
                    .title("Hello!")
                    .version("1.0beta6")
                    .withTypes(
                            TypeDeclarationBuilder.typeDeclaration("Foo").ofType(
                                    TypeBuilder.type("object")
                                            .withAnnotations(AnnotationBuilder.annotation("Foo").withProperties(PropertyValueBuilder.property("time", "2022-02-02")))
                                            ),
                            TypeDeclarationBuilder.typeDeclaration("Goo").ofType(TypeBuilder.type("object"))
                    )
                    .withAnnotationTypes(
                            AnnotationTypeBuilder.annotationType("Foo").withProperty(property("time", "date-only"))
                    )
                    .withResources(
                            resource("/no")
                                    .description("fooo!!!")
                                    .displayName("Mama!!!")
                                    .with(
                                    method("get")
                                            .withQueryParameter(ParameterBuilder.parameter("apaaa").ofType("integer").withFacets(FacetBuilder.facet("minimum").value(44)))
                                            .withAnnotations(AnnotationBuilder.annotation("Foo").withProperties(PropertyValueBuilder.property("time", "2022-02-02")))
                                            .with(key("description", "Hello"))
                                            .withBodies(
                                                    BodyBuilder.body("application/json")
                                                            .ofType(TypeBuilder.type("Foo", "Goo")
                                                                    .withProperty(TypePropertyBuilder.property("foo", "string"))
                                                            )
                                            ).withResponses(response(200))
                            )
                    ).buildModel();

            //      Resource r =
            //      Modification.add(api, r);

            final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
            Node node = ((NodeModel) api).getNode();
            grammarPhase.apply(node);

            List<ErrorNode> errors = node.findDescendantsWith(ErrorNode.class);
            for (ErrorNode error : errors) {
                System.err.println("error: " + error.getErrorMessage());
            }
            System.err.println();
            if (errors.size() == 0) {
                Emitter emitter = new Emitter();
                emitter.emit(api);

                StringWriter writer = new StringWriter();
                emitter.emit(api, writer);

                RamlModelResult re_read = new RamlModelBuilder().buildApi(new StringReader(writer.toString()),  ".");
                if (ramlModelResult.hasErrors()) {
                    for (ValidationResult validationResult : re_read.getValidationResults()) {
                        System.err.println(validationResult);
                    }
                }

                TypeDeclaration td = re_read.getApiV10().types().get(0);
                td.name();

            }


            ResourceBuilder rb = resource("/no")
                    .description("fooo!!!")
                    .displayName("Mama!!!")
                    .with(
                            method("get")
                                    .withQueryParameter(ParameterBuilder.parameter("apaaa").ofType("integer").withFacets(FacetBuilder.facet("minimum").value(44)))
                                    .withAnnotations(AnnotationBuilder.annotation("Foo").withProperties(PropertyValueBuilder.property("time", "2022-02-02")))
                                    .with(key("description", "Hello"))
                                    .withBodies(
                                            BodyBuilder.body("application/json")
                                                    .ofType(TypeBuilder.type("Foo", "Goo")
                                                            .withProperty(TypePropertyBuilder.property("foo", "string"))
                                                    )
                                    ).withResponses(response(200))
                    );
        }
    }
}
