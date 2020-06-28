package org.raml.builder;

import amf.client.model.domain.DataNode;
import amf.client.model.domain.ScalarNode;
import amf.client.model.domain.ScalarShape;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created. There, you have it.
 */
public class EnumShapeBuilder extends TypeShapeBuilder<ScalarShape, EnumShapeBuilder> {

    private String type;
    private List<DataNode> enumValues;

    public EnumShapeBuilder() {
    }

    public EnumShapeBuilder enumValues(String... enumValues) {

        this.type = "http://www.w3.org/2001/XMLSchema#string";
        this.enumValues = Arrays.stream(enumValues)
                .map(x -> new ScalarNode(x, "http://www.w3.org/2001/XMLSchema#string"))
                .collect(Collectors.toList());
        return this;
    }

    public EnumShapeBuilder enumValues(long... enumValues) {

        this.type = "http://www.w3.org/2001/XMLSchema#integer";
        this.enumValues = Arrays.stream(enumValues)
                .mapToObj(Long::toString)
                .map(x -> new ScalarNode(x, "http://www.w3.org/2001/XMLSchema#integer"))
                .collect(Collectors.toList());
        return this;
    }

    public EnumShapeBuilder enumValues(boolean... enumValues) {

        this.type = "http://www.w3.org/2001/XMLSchema#boolean";
        this.enumValues = IntStream.range(0, enumValues.length)
                .mapToObj(idx -> enumValues[idx])
                .map(x -> Boolean.toString(x))
                .map(x -> new ScalarNode(x, "http://www.w3.org/2001/XMLSchema#boolean"))
                .collect(Collectors.toList());
        return this;
    }


    @Override
    protected ScalarShape buildNodeLocally() {

        ScalarShape shape = new ScalarShape();
        shape.withDataType(type); // todo fix me!
        commonNodeInfo(shape);
        shape.withValues(enumValues);

        return shape;
    }

    public String id() {

        return "[enum]";
    }
}
