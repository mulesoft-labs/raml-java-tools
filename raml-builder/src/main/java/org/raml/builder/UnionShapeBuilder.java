package org.raml.builder;

import amf.client.model.domain.Shape;
import amf.client.model.domain.UnionShape;
import com.google.common.base.Suppliers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class UnionShapeBuilder extends TypeShapeBuilder<UnionShape, UnionShapeBuilder> {


    private Shape[] types;

    private final List<PropertyShapeBuilder> properties = new ArrayList<>();

    private final Supplier<UnionShape> response;

    public UnionShapeBuilder(Shape... types) {

        this.types = types;
        this.response = Suppliers.memoize(this::calculateNodeShape);
    }


    public UnionShapeBuilder withProperty(PropertyShapeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }


    @Override
    public UnionShape buildNodeLocally() {

        return response.get();
    }

    public UnionShape calculateNodeShape() {
        UnionShape unionShape = new UnionShape();
        commonNodeInfo(unionShape);
        unionShape.withName("anonymous");
     //   unionShape.withId("amf://id#" + (currentid ++));

        if ( types != null && types.length != 0) {
                //Not sure....
                unionShape.withAnyOf(Arrays.asList(types));
        }

        return unionShape;
    }


    public String id() {

        if (types.length == 1) {
            return "anonymous";
        } else {

            return "[" + Arrays.stream(types).map(t -> t.name().value()).collect(Collectors.joining(",")) + "]";
        }
    }
}
