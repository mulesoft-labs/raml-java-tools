package org.raml.builder;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.UnionShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class UnionShapeBuilder extends TypeShapeBuilder<UnionShape, UnionShapeBuilder> {


    private List<TypeShapeBuilder<?,?>> types;

    private final List<PropertyShapeBuilder> properties = new ArrayList<>();

    public UnionShapeBuilder(TypeShapeBuilder<?,?>... types) {

        this.types = Arrays.asList(types);
    }


    public UnionShapeBuilder withProperty(PropertyShapeBuilder... properties) {

        this.properties.addAll(Arrays.asList(properties));
        return this;
    }

    private static AnyShape doUnion(TypeShapeBuilder<?,?> t) {
        AnyShape nodeShape = t.buildReference();

        return nodeShape;
    }

    public UnionShape buildNodeLocally() {
        UnionShape unionShape = new UnionShape();
        commonNodeInfo(unionShape);
      //  unionShape.withName("anonymous");
     //   unionShape.withId("amf://id#" + (currentid ++));

        if ( types != null && types.size() != 0) {
                //Not sure....
            unionShape.withAnyOf(types.stream().map(UnionShapeBuilder::doUnion).collect(Collectors.toList()));
        }

        return unionShape;
    }

    @Override
    protected UnionShape buildReferenceShape() {
        return new UnionShape();
    }
}
