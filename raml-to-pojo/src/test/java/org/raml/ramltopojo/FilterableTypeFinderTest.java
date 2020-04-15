package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.Shape;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created. There, you have it.
 */
public class FilterableTypeFinderTest {

    @Test
    public void findTypes() {

        Document doc = RamlLoader.load(this.getClass().getResource("big-filter.raml"));
        FilterableTypeFinder finder = new FilterableTypeFinder();
        ArrayList<Shape> result = new ArrayList<>();
        finder.findTypes(doc, (p) -> true, (p, s) -> result.add(s));

        assertThat(result)
                .hasSize(4)
                    .extracting(s -> s.name().value()).containsExactly("someint", "foo", "foo", "foo");
    }
}