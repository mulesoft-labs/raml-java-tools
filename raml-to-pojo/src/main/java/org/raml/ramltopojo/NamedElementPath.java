package org.raml.ramltopojo;

import amf.client.model.domain.EndPoint;
import amf.client.model.domain.NamedDomainElement;
import amf.client.model.domain.Operation;
import amf.client.model.domain.Parameter;
import amf.core.model.domain.DomainElement;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created. There, you have it.
 */
@AllArgsConstructor
public class NamedElementPath {

    private final List<NamedDomainElement> domainElements;

    public static NamedElementPath root() {
        return new NamedElementPath(Collections.emptyList());
    }

    public <T extends NamedDomainElement> NamedElementPath append(T... elements) {

        ArrayList<NamedDomainElement> domainElements = new ArrayList<>(this.domainElements);
        domainElements.addAll(Arrays.asList(elements));
        return new NamedElementPath(domainElements);
    }
}
