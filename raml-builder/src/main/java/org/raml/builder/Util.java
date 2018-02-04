package org.raml.builder;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.model.DefaultModelElement;
import org.raml.v2.internal.impl.commons.model.StringType;
import org.raml.v2.internal.impl.commons.model.factory.TypeDeclarationModelFactory;
import org.raml.v2.internal.impl.commons.phase.*;
import org.raml.v2.internal.impl.v10.Raml10Builder;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.phase.AnnotationValidationPhase;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.v10.phase.MediaTypeInjectionPhase;
import org.raml.v2.internal.impl.v10.phase.ReferenceResolverTransformer;
import org.raml.yagi.framework.model.*;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.GrammarPhase;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.phase.TransformationPhase;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.raml.v2.api.RamlModelBuilder.MODEL_PACKAGE;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created. There, you have it.
 */
public class Util {
    public static ModelBindingConfiguration bindingConfiguration() {

        final DefaultModelBindingConfiguration bindingConfiguration = new DefaultModelBindingConfiguration();
        bindingConfiguration.bindPackage(MODEL_PACKAGE);
        // Bind all StringTypes to the StringType implementation they are only marker interfaces
        bindingConfiguration.bind(org.raml.v2.api.model.v10.system.types.StringType.class, StringType.class);
        bindingConfiguration.bind(org.raml.v2.api.model.v10.system.types.ValueType.class, StringType.class);
        bindingConfiguration.defaultTo(DefaultModelElement.class);
        bindingConfiguration.bind(TypeDeclaration.class, new TypeDeclarationModelFactory());
        bindingConfiguration.reverseBindPackage("org.raml.v2.api.model.v10.datamodel");
        return bindingConfiguration;
    }

    public static <T> T buildModel(ModelBindingConfiguration binding, Node node, Class<T> cls) {
        NodeModelFactory fac = binding.bindingOf(cls);
        NodeModel model = fac.create(node);
        T m = ModelProxyBuilder.createModel(cls, model, binding);

        final Phase grammarPhase = createPhases(new ResourceLoader() {
            @Nullable
            @Override
            public InputStream fetchResource(String resourceName) {
                return null;
            }
        }, new RamlHeader(RAML_10, Default).getFragment());
        Node newNode = ((NodeModel) m).getNode();
        grammarPhase.apply(newNode);

        List<ErrorNode> errors = node.findDescendantsWith(ErrorNode.class);
        if (!errors.isEmpty()) {

            throw new ModelBuilderException(errors);
        }

        return m;
    }

    private static Phase createPhases(ResourceLoader resourceLoader, RamlFragment fragment) {
        final TransformationPhase ramlFragmentsValidator = new TransformationPhase(new RamlFragmentGrammarTransformer(new Raml10Builder(), resourceLoader));

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(fragment));

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

                List<Phase> phases = Arrays.asList(
                        //      includePhase,
                        ramlFragmentsValidator,
                        grammarPhase,
                        referenceCheck,
                        resourcePhase,
                        duplicatedPaths,
                        checkUnusedParameters,
                        annotationValidationPhase,
                        mediaTypeInjection,
                        grammarPhase,
                        schemaValidationPhase,
                        typeValidationPhase,
                        exampleValidationPhase
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
