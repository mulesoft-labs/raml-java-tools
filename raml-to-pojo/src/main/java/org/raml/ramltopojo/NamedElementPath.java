package org.raml.ramltopojo;

import amf.client.model.StrField;
import amf.client.model.domain.*;
import com.google.common.base.Suppliers;
import lombok.*;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import amf.client.model.document.Module;

/**
 * Created. There, you have it.
 */
@AllArgsConstructor @ToString(of={"domainElements"})
public class NamedElementPath {

    @RequiredArgsConstructor(access=AccessLevel.PRIVATE)
    private static class NameTypePair {
        private final Object object;
        private final String objectName;

        public String toString() {
            return objectName + "(" + object.getClass().getSimpleName()  + ")";
        }
    }
    public static NameTypePair pair(Object target, String name) {

        return new NameTypePair(target, name);
    }

    public static NameTypePair pair(Object target, StrField name) {

        return new NameTypePair(target, name.value());
    }

    @Getter(AccessLevel.PRIVATE)
    private final List<NameTypePair> domainElements;
    private final Supplier<List<String>> names = Suppliers.memoize(() -> getDomainElements().stream().map(d -> d.objectName).collect(Collectors.toList()));
    private final Supplier<List<Class<?>>> classes = Suppliers.memoize(() -> getDomainElements().stream().map(d -> d.object.getClass()).collect(Collectors.toList()));

    public static NamedElementPath root() {
        return new NamedElementPath(Collections.emptyList());
    }


    public NamedElementPath append(Module m) {

        ArrayList<NameTypePair> domainElements = new ArrayList<>(this.domainElements);
        domainElements.add(new NameTypePair(m, m.location()));
        return new NamedElementPath(domainElements);
    }

    public NamedElementPath append(Shape m) {

        ArrayList<NameTypePair> domainElements = new ArrayList<>(this.domainElements);
        domainElements.add(new NameTypePair(m, m.name().value()));
        return new NamedElementPath(domainElements);
    }

    public NamedElementPath append(EndPoint m) {

        ArrayList<NameTypePair> domainElements = new ArrayList<>(this.domainElements);
        domainElements.add(new NameTypePair(m, m.path().value()));
        return new NamedElementPath(domainElements);
    }

    public NamedElementPath append(Operation m) {

        ArrayList<NameTypePair> domainElements = new ArrayList<>(this.domainElements);
        domainElements.add(new NameTypePair(m, m.method().value()));
        return new NamedElementPath(domainElements);
    }

    public NamedElementPath append(Response m) {

        ArrayList<NameTypePair> domainElements = new ArrayList<>(this.domainElements);
        domainElements.add(new NameTypePair(m, m.statusCode().value()));
        return new NamedElementPath(domainElements);
    }

    public NamedElementPath append(Payload m) {

        ArrayList<NameTypePair> domainElements = new ArrayList<>(this.domainElements);
        domainElements.add(new NameTypePair(m, m.mediaType().value()));
        return new NamedElementPath(domainElements);
    }

    public NamedElementPath append(Parameter m) {

        ArrayList<NameTypePair> domainElements = new ArrayList<>(this.domainElements);
        domainElements.add(new NameTypePair(m, m.name().value()));
        return new NamedElementPath(domainElements);
    }

    public List<String> names() {
        return names.get();
    }

    public List<Class<?>> classes() {
        return classes.get();
    }

    public Optional<AnyShape> anyShape() {

        return shape(AnyShape.class);
    }

    public <T extends AnyShape> Optional<T> shape(Class<T> cls) {
        if (domainElements.size() == 0 || !cls.isAssignableFrom(domainElements.get(domainElements.size() - 1).getClass())) {
            return Optional.empty();
        } else {
            T t = (T) domainElements.get(domainElements.size() - 1).object;
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


    public boolean entirelyMatches(Class<?>... path) {

        if ( domainElements.size() - path.length != 0) {
            return false;
        }

        return IntStream.range(0, path.length).allMatch(getDiffablePredicate(0, path));
    }

    public boolean entirelyMatches(String... path) {

        if ( domainElements.size() - path.length != 0) {
            return false;
        }

        return IntStream.range(0, path.length).allMatch(getDiffablePredicate(0, path));
    }

    private IntPredicate getDiffablePredicate(int diff, Class<?>[] path) {
        return i -> path[i].isAssignableFrom(domainElements.get(i + diff).object.getClass());
    }

    private IntPredicate getDiffablePredicate(int diff, String[] path) {
        return i -> path[i].equals(domainElements.get(i + diff).objectName);
    }

}
