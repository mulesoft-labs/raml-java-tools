package org.raml.simpleemitter;


import org.raml.builder.*;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.phase.*;
import org.raml.v2.internal.impl.v10.Raml10Builder;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.phase.AnnotationValidationPhase;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.v10.phase.MediaTypeInjectionPhase;
import org.raml.v2.internal.impl.v10.phase.ReferenceResolverTransformer;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.GrammarPhase;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.phase.TransformationPhase;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.raml.builder.MethodBuilder.method;
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

        Api api = document()
                .baseUri("http:fun.com/fun")
                .title("Hello!")
                .version("1.0beta6")
                .withTypes(
                        TypeDeclarationBuilder.typeDeclaration("EnumFoo").ofType(TypeBuilder.type().enumValues("UN", "DEUX")),
                        TypeDeclarationBuilder.typeDeclaration("EnumNum").ofType(TypeBuilder.type("integer").enumValues(1,2)),

                        TypeDeclarationBuilder.typeDeclaration("Foo").ofType(
                                TypeBuilder.type("object")
                                        .withFacets(FacetBuilder.facet("required").ofType("boolean"))
                                        .withAnnotations(AnnotationBuilder.annotation("Foo")
                                                .withProperties(PropertyValueBuilder.property("time", "2022-02-02"), PropertyValueBuilder.propertyOfArray("count", 1,2)))
                        ),
                        TypeDeclarationBuilder.typeDeclaration("Goo").ofType(TypeBuilder.type("object"))
                )
                .withAnnotationTypes(
                        AnnotationTypeBuilder.annotationType("Foo").withProperty(property("time", "date-only")).withProperty(property("count", "integer[]"))
                )
                .withResources(
                        resource("/no")
                                .description("fooo!!!")
                                .displayName("Mama!!!")
                                .with(
                                        method("get")
                                                .description("fooofooofooo")
                                                .withQueryParameter(ParameterBuilder.parameter("apaaa").ofType("integer"))
                                                .withAnnotations(AnnotationBuilder.annotation("Foo").withProperties(
                                                        PropertyValueBuilder.property("time", "2022-02-02"),
                                                        PropertyValueBuilder.propertyOfArray("count", 7)))
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

//        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
        final Phase grammarPhase = createPhases(null, new RamlHeader(RAML_10, Default).getFragment());

        Node node = ((NodeModel) api).getNode();
        grammarPhase.apply(node);

        List<ErrorNode> errors = node.findDescendantsWith(ErrorNode.class);
        for (ErrorNode error : errors) {
            System.err.println("error: " + error.getErrorMessage());
        }
        StringTypeDeclaration stdzero = (StringTypeDeclaration) api.types().get(0);
        System.err.println(stdzero.enumValues());

        System.err.println(api.types().get(0).name());
        System.err.println();
        if (errors.size() == 0) {
            Emitter emitter = new Emitter();
            emitter.emit(api);

            StringWriter writer = new StringWriter();
            emitter.emit(api, writer);

            RamlModelResult re_read = new RamlModelBuilder().buildApi(new StringReader(writer.toString()), ".");
            if (re_read.hasErrors()) {
                for (ValidationResult validationResult : re_read.getValidationResults()) {
                    System.err.println(validationResult);
                }
            }

            StringTypeDeclaration std = (StringTypeDeclaration) re_read.getApiV10().types().get(0);
            System.err.println(std.enumValues());
        }


    }

    private static Phase createPhases(ResourceLoader resourceLoader, RamlFragment fragment)
    {
        // The first phase expands the includes.
        final TransformationPhase includePhase = new TransformationPhase(new IncludeResolver(resourceLoader), new StringTemplateExpressionTransformer());

        final TransformationPhase ramlFragmentsValidator = new TransformationPhase(new RamlFragmentGrammarTransformer(new Raml10Builder(), resourceLoader));

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(fragment));

        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // sugar
        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???/
        //final TransformationPhase libraryLink = new TransformationPhase(new LibraryLinkingTransformation(this, resourceLoader));

        final TransformationPhase referenceCheck = new TransformationPhase(new ReferenceResolverTransformer());

        // Applies resourceTypes and Traits Library
        final TransformationPhase resourcePhase = new TransformationPhase(new ResourceTypesTraitsTransformer(new Raml10Grammar()));

        final TransformationPhase duplicatedPaths = new TransformationPhase(new DuplicatedPathsTransformer());

        // Check unused uri parameters
        final TransformationPhase checkUnusedParameters = new TransformationPhase(new UnusedParametersTransformer());

        // Run grammar again to re-validate tree

        final AnnotationValidationPhase annotationValidationPhase = new AnnotationValidationPhase(resourceLoader);

        final MediaTypeInjectionPhase mediaTypeInjection = new MediaTypeInjectionPhase();

        // Schema Types example validation
        final TransformationPhase schemaValidationPhase = new TransformationPhase(new SchemaValidationTransformer(resourceLoader));

        // Checks types consistency and custom facets
        final TypeValidationPhase typeValidationPhase = new TypeValidationPhase();

        final ExampleValidationPhase exampleValidationPhase = new ExampleValidationPhase(resourceLoader);

        Phase phase = new Phase() {
            @Override
            public Node apply(Node tree) {

                List<GrammarPhase> phases = Arrays.asList(
                        //      includePhase,
                        //      ramlFragmentsValidator,
                        grammarPhase
                        //      libraryLink,
   //                           referenceCheck,
   //                          resourcePhase,
   //                          duplicatedPaths,
   //                          checkUnusedParameters,
   //                          annotationValidationPhase,
   //                          mediaTypeInjection,
   //                          grammarPhase,
   //                          schemaValidationPhase,
             //           typeValidationPhase,
    //                    exampleValidationPhase
                );

                Node node = null;
                for (Phase phase : phases) {
                    node = phase.apply(tree);
                }

                return node;
            }
        };

        return phase;
    }

}
