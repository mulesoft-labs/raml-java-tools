package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.ScalarShape;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

/**
 * Created. There, you have it.
 */
public class ScalarTypesTest {

    @Test
    public void checkScalarTypes() throws ExecutionException, InterruptedException {

        Document api = RamlLoader.load(this.getClass().getResource("scalar-types.raml"));
        NodeShape shape = RamlLoader.findShape("foo", api.declares());

        assertEquals(ScalarTypes.INTEGER_SCALAR, rangeOf(shape, "integer").dataType().value());
        assertEquals(ScalarTypes.DATETIME_ONLY_SCALAR, rangeOf(shape, "datetime-only").dataType().value());
        assertEquals(ScalarTypes.STRING_SCALAR, rangeOf(shape, "string").dataType().value());
        assertEquals(ScalarTypes.NUMBER_SCALAR, rangeOf(shape, "number").dataType().value());
        assertEquals(ScalarTypes.DATE_ONLY_SCALAR, rangeOf(shape, "date-only").dataType().value());
        assertEquals(ScalarTypes.BOOLEAN_SCALAR, rangeOf(shape, "boolean").dataType().value());
        assertEquals(ScalarTypes.TIME_ONLY_SCALAR, rangeOf(shape, "time-only").dataType().value());

    }

    protected ScalarShape rangeOf(NodeShape shape, String scalarType) {
        return (ScalarShape) shape.properties().stream().filter((x -> x.name().is(scalarType))).findFirst().get().range();
    }
}