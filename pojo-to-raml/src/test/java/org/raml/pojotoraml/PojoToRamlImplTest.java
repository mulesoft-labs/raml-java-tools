package org.raml.pojotoraml;

import org.junit.Test;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.pojotoraml.field.FieldClassParser;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created. There, you have it.
 */
public class PojoToRamlImplTest {
    @Test
    public void pojoToRamlTypeBuilder() throws Exception {

        PojoToRamlImpl pojoToRaml = new PojoToRamlImpl(FieldClassParser.factory(), RamlAdjuster.NULL_ADJUSTER);
        Result types =  pojoToRaml.classToRaml(Fun.class);

        RamlDocumentBuilder ramlDocumentBuilder = RamlDocumentBuilder
                .document()
                .baseUri("http://google.com")
                .title("hello")
                .version("1")
                .withTypes(types.allTypes().toArray(new TypeDeclarationBuilder[0]));

        Api api = ramlDocumentBuilder.buildModel();

        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
        Node node = ((NodeModel) api).getNode();
        Node checked = grammarPhase.apply(node);
        List<ErrorNode> errors = checked.findDescendantsWith(ErrorNode.class);
        for (ErrorNode error : errors) {
            System.err.println("error: " + error.getErrorMessage());
        }

        List<TypeDeclaration> buildTypes = api.types();

        assertEquals(2, buildTypes.size());
        assertEquals("Fun", buildTypes.get(0).name());
        assertEquals("SubFun", buildTypes.get(1).name());

    }

    private List<Phase> createPhases(ResourceLoader resourceLoader, RamlFragment fragment)
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

        return Arrays.asList(
          //      includePhase,
          //      ramlFragmentsValidator,
                grammarPhase,
          //      libraryLink,
          //      referenceCheck,
           //     resourcePhase,
           //     duplicatedPaths,
           //     checkUnusedParameters,
           //     annotationValidationPhase,
           //     mediaTypeInjection,
           //     grammarPhase,
           //     schemaValidationPhase,
           //     typeValidationPhase,
                exampleValidationPhase
                 );

    }


}