package org.raml.ramltopojo;

import amf.core.model.domain.DomainElement;
import amf.core.model.domain.NamedDomainElement;
import lombok.AllArgsConstructor;

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
}
