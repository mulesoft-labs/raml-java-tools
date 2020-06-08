package org.raml.builder;

import amf.client.model.domain.DomainElement;
import com.google.common.base.Joiner;
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
public class TypeShapeBuilder extends NodeShapeBuilder<TypeShapeBuilder> implements NodeBuilder, AnnotableBuilder<TypeShapeBuilder> {

    private List<PropertyShapeBuilder> properties = new ArrayList<>();
    private List<ExamplesBuilder> examples = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();
    private List<FacetBuilder> facets = new ArrayList<>();


    public String[] types;
    private String description;
    private ValueNodeFactory enumValues;

    private ExamplesBuilder example;

    private TypeShapeBuilder arrayItems;

    private TypeShapeBuilder(String type) {
        this.types= new String[] {type};
    }

    public TypeShapeBuilder(String[] types) {

        this.types = types;
    }

    public TypeShapeBuilder(TypeShapeBuilder builder) {
        this.types = new String[] {"array"};
        this.arrayItems = builder;
    }

    public String id() {

        if (types.length == 1) {
            return types[0];
        } else {

            return "[" + Joiner.on(",").join(types) + "]";
        }
    }

    static public TypeShapeBuilder type(String type) {

        return new TypeShapeBuilder(type);
    }

    static public TypeShapeBuilder arrayOf(TypeShapeBuilder builder) {

        return new TypeShapeBuilder(builder);
    }

    static public TypeShapeBuilder type() {

        return new TypeShapeBuilder((String[])null);
    }

    static public TypeShapeBuilder type(String... types) {

        return new TypeShapeBuilder(types);
    }

    @Override
    public TypeShapeBuilder withAnnotations(AnnotationBuilder... builders) {

        this.annotations.addAll(Arrays.asList(builders));
        return this;
    }

    public TypeShapeBuilder withProperty(PropertyShapeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }

    public TypeShapeBuilder withExamples(ExamplesBuilder... properties) {

        this.example = null;
        this.examples.addAll(Arrays.asList(properties));
        return this;
    }

    public TypeShapeBuilder withExample(ExamplesBuilder example) {

        this.examples.clear();
        this.example = example;
        return this;
    }

    public TypeShapeBuilder withFacets(FacetBuilder... facetBuilders) {

        this.facets.addAll(Arrays.asList(facetBuilders));
        return this;
    }

    public TypeShapeBuilder description(String description) {

        this.description = description;
        return this;
    }

    public TypeShapeBuilder enumValues(String... enumValues) {

        this.enumValues = ValueNodeFactories.create(new SimpleSYArrayNode(), enumValues);
        return this;
    }

    public TypeShapeBuilder enumValues(long... enumValues) {

        this.enumValues = ValueNodeFactories.create(new SimpleSYArrayNode(), enumValues);
        return this;
    }
    public TypeShapeBuilder enumValues(boolean... enumValues) {

        this.enumValues = ValueNodeFactories.create(new SimpleSYArrayNode(), enumValues);
        return this;
    }

    @Override
    public DomainElement buildNode() {

        ObjectNode node = super.buildNode();

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


        if ( ! facets.isEmpty() ) {

            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("facets"), new ObjectNodeImpl());
            for (FacetBuilder facetBuilder : facets) {
                kvn.getValue().addChild(facetBuilder.buildNode());
            }

            node.addChild(kvn);
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
            for (PropertyShapeBuilder property : properties) {
                kvn.getValue().addChild(property.buildNode());
            }

            node.addChild(kvn);
        }

        if ( ! examples.isEmpty() ) {

            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("examples"), new ObjectNodeImpl());
            for (ExamplesBuilder example : examples) {
                    kvn.getValue().addChild(example.buildNode());
            }

        //    node.addChild(kvn);
        }

        if ( example != null ) {

            KeyValueNodeImpl kvn2 = new KeyValueNodeImpl(new StringNodeImpl("example"), example.buildNode().getValue());
            node.addChild(kvn2);
        }

        if (  arrayItems != null  ) {

            KeyValueNodeImpl kvn = new KeyValueNodeImpl(new StringNodeImpl("items"), arrayItems.buildNode());
            node.addChild(kvn);
        }

        return node;
    }

}
