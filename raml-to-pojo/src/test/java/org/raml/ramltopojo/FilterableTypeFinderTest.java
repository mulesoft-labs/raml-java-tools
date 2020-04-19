package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.*;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Condition;
import org.junit.Test;
import amf.client.model.document.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.raml.ramltopojo.FilterableTypeFinderTest.AllMatchesExactly.thatAllMatchExactly;
import static org.raml.ramltopojo.FilterableTypeFinderTest.ClassesMatchesExactly.*;


/**
 * Created. There, you have it.
 */
public class FilterableTypeFinderTest {

    @Test
    public void findTopTypes() {

        ArrayList<NamedElementPath> result = buildResults();

        assertThat(result)
                .areAtLeastOne(thatAllMatchExactly("someint", ScalarShape.class))
                .areAtLeastOne(thatAllMatchExactly("foo", NodeShape.class))
                .areAtLeastOne(thatAllMatchExactly("somebool", NodeShape.class));
    }

    @Test
    public void findParametersInRequest() {

        ArrayList<NamedElementPath> result = buildResults();

        assertThat(result)
                .areAtLeastOne(thatAllMatchExactly("/first", EndPoint.class, "put", Operation.class, "q1", Parameter.class, "schema", ScalarShape.class))
                .areAtLeastOne(thatAllMatchExactly("/first", EndPoint.class, "put", Operation.class, "h1", Parameter.class, "schema", ScalarShape.class));
    }

    @Test
    public void findRequestSchemas() {

        ArrayList<NamedElementPath> result = buildResults();

        assertThat(result)
                .areAtLeastOne(thatAllMatchExactly("/first", EndPoint.class, "put", Operation.class, "application/json", Payload.class, "haha", ScalarShape.class))
                .areAtLeastOne(thatAllMatchExactly("/first/deep", EndPoint.class, "put", Operation.class, "application/json", Payload.class, "foo", NodeShape.class));
    }
    @Test
    public void findResponses() {

        ArrayList<NamedElementPath> result = buildResults();

        assertThat(result)
                .areAtLeastOne(thatAllMatchExactly("/first", EndPoint.class, "put", Operation.class, "200", Response.class, "application/json", Payload.class, "foo", NodeShape.class));
    }

    @Test
    public void findResponseHeaders() {

        ArrayList<NamedElementPath> result = buildResults();

        assertThat(result)
                .areAtLeastOne(thatAllMatchExactly("/first", EndPoint.class, "put", Operation.class, "200", Response.class, "h2", Parameter.class, "schema", ScalarShape.class));
    }

    @Test
    public void findModules() {

        ArrayList<NamedElementPath> result = buildResults();

        assertThat(result)
                .areAtLeastOne(thatClassesMatchExactly(Module.class, ScalarShape.class));
    }


    private ArrayList<NamedElementPath> buildResults() {
        Document doc = RamlLoader.load(this.getClass().getResource("big-filter.raml"));
        FilterableTypeFinder finder = new FilterableTypeFinder();
        ArrayList<NamedElementPath> result = new ArrayList<>();
        finder.findTypes(doc, (p) -> true, (p, s) -> result.add(p.append(s)));
        return result;
    }


    @AllArgsConstructor
    static class ClassesMatchesExactly extends Condition<NamedElementPath> {

        private final Class<?>[] listOfClasses;

        public static ClassesMatchesExactly thatClassesMatchExactly(Class<?>... dom) {
            return new ClassesMatchesExactly(dom);
        }

        @Override
        public boolean matches(NamedElementPath value) {
            return value.entirelyMatches(listOfClasses);
        }
    }

    @AllArgsConstructor
    static class AllMatchesExactly extends Condition<NamedElementPath> {

        private final Object[] listOfStuff;

        public static AllMatchesExactly thatAllMatchExactly(Object... dom) {
            return new AllMatchesExactly(dom);
        }

        @Override
        public boolean matches(NamedElementPath value) {

            return value.entirelyMatches(Arrays.stream(listOfStuff).filter(s -> s instanceof Class<?>).map(s -> (Class<?>) s).toArray(Class[]::new))
                    && value.entirelyMatches(Arrays.stream(listOfStuff).filter(s -> s instanceof String).map(s -> (String) s).toArray(String[]::new));
        }
    }

}