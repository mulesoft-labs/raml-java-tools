package org.raml.builder;

import org.raml.yagi.framework.nodes.KeyValueNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class ParameterBuilder extends KeyValueNodeBuilder<ParameterBuilder> {

    private String type;
    private String displayName;
    private String description;
    private List<FacetBuilder> facets = new ArrayList<>();
    private Boolean required;

    public ParameterBuilder(String name) {
        super(name);
    }

    public static ParameterBuilder parameter(String name) {

        return new ParameterBuilder(name);
    }

    public ParameterBuilder ofType(String name) {

        this.type = name;
        return this;
    }

    public ParameterBuilder withFacets(FacetBuilder... builders) {
        this.facets.addAll(Arrays.asList(builders));
        return this;
    }

    @Override
    public KeyValueNode buildNode() {

        KeyValueNode node = super.buildNode();

        addProperty(node.getValue(), "type", type);
        addProperty(node.getValue(), "displayName", displayName);
        addProperty(node.getValue(), "description", description);
        addProperty(node.getValue(), "required", required);

        if ( ! facets.isEmpty() ) {

            for (FacetBuilder facet : facets) {
                node.getValue().addChild(facet.buildNode());
            }
        }


        return node;
    }

    public ParameterBuilder displayName(String displayName) {

        this.displayName = displayName;
        return this;
    }

    public ParameterBuilder description(String description) {

        this.description = description;
        return this;
    }

    public ParameterBuilder required(boolean required) {

        this.required = required;
        return this;
    }
}
