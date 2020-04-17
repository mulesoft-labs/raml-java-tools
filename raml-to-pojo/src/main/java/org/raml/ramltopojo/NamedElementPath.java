package org.raml.ramltopojo;

import amf.client.model.domain.*;
import amf.core.model.domain.DomainElement;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.reverse;

/**
 * Created. There, you have it.
 */
@AllArgsConstructor
public class NamedElementPath {
    @Getter(AccessLevel.PRIVATE)
    private final List<NamedDomainElement> domainElements;
    private final Supplier<List<String>> names = Suppliers.memoize(() -> getDomainElements().stream().map(d -> d.name().value()).collect(Collectors.toList()));
    private final Supplier<List<Class<? extends NamedDomainElement>>> classes = Suppliers.memoize(() -> getDomainElements().stream().map(NamedDomainElement::getClass).collect(Collectors.toList()));

    public static NamedElementPath root() {
        return new NamedElementPath(Collections.emptyList());
    }

    public <T extends NamedDomainElement> NamedElementPath append(T... elements) {

        ArrayList<NamedDomainElement> domainElements = new ArrayList<>(this.domainElements);
        domainElements.addAll(Arrays.asList(elements));
        return new NamedElementPath(domainElements);
    }

    public List<String> names() {
        return names.get();
    }

    public List<Class<? extends NamedDomainElement>> classes() {
        return classes.get();
    }

    public Optional<AnyShape> anyShape() {

        return shape(AnyShape.class);
    }

    public <T extends AnyShape> Optional<T> shape(Class<T> cls) {
        if (domainElements.size() == 0 || !cls.isAssignableFrom(domainElements.get(domainElements.size() - 1).getClass())) {
            return Optional.empty();
        } else {
            T t = (T) domainElements.get(domainElements.size() - 1);
            return Optional.of(t);
        }
    }

    public boolean endMatches(Class<? extends NamedDomainElement>... path) {

        int diff = domainElements.size() - path.length;
        if ( diff < 0) {
            return false;
        }

        return IntStream.range(0, path.length).allMatch(getDiffablePredicate(diff, path));
    }

    private IntPredicate getDiffablePredicate(int diff, Class<? extends NamedDomainElement>[] path) {
        return i -> path[i].isAssignableFrom(domainElements.get(i + diff).getClass());
    }

    public boolean entirelyMatches(Class<? extends NamedDomainElement>... path) {

        if ( domainElements.size() - path.length != 0) {
            return false;
        }

        return IntStream.range(0, path.length).allMatch(getDiffablePredicate(0, path));
    }

}
