package org.raml.builder;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.raml.builder.NodeBuilders.arrayProperty;
import static org.raml.builder.NodeBuilders.property;

/**
 * Created. There, you have it.
 */
public class TypeBuilder extends ObjectNodeBuilder<TypeBuilder> implements NodeBuilder, AnnotableBuilder<TypeBuilder> {

    private List<NodeBuilder> properties = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();

    public String[] types;
    private String description;
    private String[] enumValues;

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

    @Override
    public ObjectNode buildNode() {

        TypeDeclarationNode node = new TypeDeclarationNode();

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

            node.addChild(arrayProperty("enumValues", enumValues).buildNode());
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

        return node;
    }

    public TypeBuilder withProperty(TypePropertyBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }

    public TypeBuilder description(String description) {

        this.description = description;
        return this;
    }

    public TypeBuilder enumValues(String... enumValues) {

        this.enumValues = enumValues;
        return this;
    }

}
