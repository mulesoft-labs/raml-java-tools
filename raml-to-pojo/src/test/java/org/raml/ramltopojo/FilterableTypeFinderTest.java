package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.*;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.raml.ramltopojo.FilterableTypeFinderTest.MatchesExactly.*;


/**
 * Created. There, you have it.
 */
public class FilterableTypeFinderTest {

    @Test
    public void findTypes() {

        Document doc = RamlLoader.load(this.getClass().getResource("big-filter.raml"));
        FilterableTypeFinder finder = new FilterableTypeFinder();
        ArrayList<NamedElementPath> result = new ArrayList<>();
        finder.findTypes(doc, (p) -> true, (p, s) -> result.add(p));

        assertThat(result)
                .hasSize(4)
                .areAtLeastOne(thatMatchesExactly(ScalarShape.class))
                .areAtLeastOne(thatMatchesExactly(NodeShape.class))
                .areAtLeastOne(thatMatchesExactly(EndPoint.class, Operation.class, Payload.class, NodeShape.class))
                .areAtLeastOne(thatMatchesExactly(EndPoint.class, Operation.class, Payload.class, ScalarShape.class));
    }

    @AllArgsConstructor
    static class MatchesExactly extends Condition<NamedElementPath> {

        private final Class<? extends NamedDomainElement>[] listOfClasses;

        @SafeVarargs
        public static MatchesExactly thatMatchesExactly(Class<? extends NamedDomainElement>... dom) {
            return new MatchesExactly(dom);
        }

        @Override
        public boolean matches(NamedElementPath value) {
            return value.entirelyMatches(listOfClasses);
        }
    }

    private AssertionError failOnType() {
        return new AssertionError("not right type");
    }
}