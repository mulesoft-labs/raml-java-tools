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
    private List<FacetBuilder> facets = new ArrayList<>();

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

        if ( ! facets.isEmpty() ) {

            for (FacetBuilder facet : facets) {
                node.getValue().addChild(facet.buildNode());
            }
        }
        return node;
    }
}
