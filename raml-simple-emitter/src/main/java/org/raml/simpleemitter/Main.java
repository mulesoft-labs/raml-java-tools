package org.raml.simpleemitter;


import org.raml.builder.AnnotationTypeBuilder;
import org.raml.builder.BodyBuilder;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.GrammarPhase;


import java.io.InputStreamReader;
import java.io.Reader;
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
            Api apiRead = ramlModelResult.getApiV10();

            Emitter emitter = new Emitter();
//
//            api = Modification.set(api, "version", "v123");


            Api api = document()
                    .withTypes(
                            TypeDeclarationBuilder.typeDeclaration("Foo").ofType(TypeBuilder.type("object")),
                            TypeDeclarationBuilder.typeDeclaration("Goo").ofType(TypeBuilder.type("object"))
                    )
                    .withAnnotationTypes(
                            AnnotationTypeBuilder.annotationType("Foo").withProperty(property("time", "date"))
                    )
                    .with(
                            key("title", "Hello!"),
                                resource("/no").with(
                                        key("displayName", "I'm happy"),
                                        method("get")
                                                .with(key("description", "Hello"))
                                                .withBodies(
                                                        BodyBuilder.body("application/json")
                                                                .ofType(TypeBuilder.type("Foo","Goo")
                                                                        .withProperty(property("foo", "string"))
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
            if (errors.size() == 0) {
                emitter.emit(api);
            }

            System.out.println();
            System.out.println(api.title().value() + ", " + api.resources());
        }
    }
}
