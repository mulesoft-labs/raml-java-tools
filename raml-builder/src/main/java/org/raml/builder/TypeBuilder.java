package org.raml.builder;

import org.raml.v2.internal.impl.commons.nodes.FacetNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.raml.builder.NodeBuilders.property;

/**
 * Created. There, you have it.
 */
public class TypeBuilder extends ObjectNodeBuilder<TypeBuilder> implements NodeBuilder, AnnotableBuilder<TypeBuilder> {

    private List<TypePropertyBuilder> properties = new ArrayList<>();
    private List<ExamplesBuilder> examples = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    public String[] types;
    private String description;
    private ValueNodeFactory enumValues;

    private TypeBuilder(String type) {
        this.types= new String[] {type};
    }

    public TypeBuilder(String[] types) {

        this.types = types;
    }

    static public TypeBuilder type(String type) {

        return new TypeBuilder(type);
    }

    static public TypeBuilder type() {

        return new TypeBuilder((String[])null);
    }

    static public TypeBuilder type(String... types) {

        return new TypeBuilder(types);
    }

    @Override
    public TypeBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

    public TypeBuilder withProperty(TypePropertyBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }

    public TypeBuilder withExamples(ExamplesBuilder... properties) {

        this.examples.addAll(Arrays.asList(properties));
        return this;
    }

    public TypeBuilder description(String description) {

        this.description = description;
        return this;
    }

    public TypeBuilder enumValues(String... enumValues) {

        this.enumValues = ValueNodeFactories.create(new SimpleSYArrayNode(), enumValues);
        return this;
    }

    public TypeBuilder enumValues(long... enumValues) {

        this.enumValues = ValueNodeFactories.create(new SimpleSYArrayNode(), enumValues);
        return this;
    }
    public TypeBuilder enumValues(boolean... enumValues) {

        this.enumValues = ValueNodeFactories.create(new SimpleSYArrayNode(), enumValues);
        return this;
    }

    @Override
    public ObjectNode buildNode() {

        ObjectNodeImpl node = new ObjectNodeImpl();

        if ( types != null ) {
            if (types.length == 1) {
                node.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl(types[0])));
            } else {

                SimpleArrayNode impl = new SimpleArrayNode();
                for (String type : types) {
                    impl.addChild(new StringNodeImpl(type));
                }
                node.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), impl));
            }
        }

        if ( description != null ) {

            node.addChild(property("description", description).buildNode());
        }

        if ( enumValues != null ) {

            FacetNode facetNode = new FacetNode();
            facetNode.addChild(new StringNodeImpl("enum"));

            facetNode.addChild(enumValues.createNode());
            node.addChild(facetNode);
        }

        if ( ! annotations.isEmpty() ) {

            for (AnnotationBuilder annotation : annotations) {
                node.addChild(annotation.buildNode());
            }
        }

        if ( ! properties.isEmpty() ) {

            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("properties"), new ObjectNodeImpl());
            for (NodeBuilder property : properties) {
                kvn.getValue().addChild(property.buildNode());
            }

            node.addChild(kvn);
        }

        if ( ! examples.isEmpty() ) {

            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("examples"), new ObjectNodeImpl());
            for (ExamplesBuilder example : examples) {
                kvn.getValue().addChild(example.buildNode());
            }

            node.addChild(kvn);
        }

        return node;
    }

}
