package org.raml.ramltopojo;

import amf.client.model.Annotable;
import amf.client.model.document.Document;
import amf.client.model.domain.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * Created. There, you have it.
 */
public class AnnotationEngine {
    public static <T> List<T> getWithDefaultList(String propName, Function<ArrayNode, List<T>> mapToType, Annotable target, Annotable... others) {
        List<T> b = evaluateAsList(propName, mapToType, target, others);
        if (b == null) {

            return emptyList();
        } else {
            return b;
        }
    }

    public static<T> List<T> evaluateAsList(String annotationField, Function<ArrayNode, List<T>> mapToType, Annotable mandatory, Annotable... others) {

        List<Annotable> targets = new ArrayList<>();
        targets.add(mandatory);
        targets.addAll(Arrays.asList(others));

        return targets.stream()
                .map(a -> arrayNodes(annotationField, a))
                .map(mapToType)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private static ArrayNode arrayNodes(String annotationField, Annotable a ) {

        DataNode node;
        if ( a instanceof Document) {

            node =  getExtension(annotationField, (Document) a).orElseGet(DomainExtension::new).extension();
        } else {

            node = getExtension(annotationField, (DomainElement)a).orElseGet(DomainExtension::new).extension();
        }

        if ( node == null || node instanceof ArrayNode ) {
            return (ArrayNode) node;
        } else {
            throw new GenerationException("expecting array for " + annotationField + " but got " + node.getClass());
        }
    }

    private static Optional<DomainExtension> getExtension(String annotationField, DomainElement a) {
        return a.customDomainProperties().stream().filter(x -> x.name().is(annotationField)).findAny();
    }

    private static Optional<DomainExtension> getExtension(String annotationField, Document a) {
        return a.encodes().customDomainProperties().stream().filter(x -> x.name().is(annotationField)).findAny();
    }
}
