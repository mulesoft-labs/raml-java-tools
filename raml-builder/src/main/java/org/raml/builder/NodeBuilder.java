package org.raml.builder;

import amf.client.model.domain.DomainElement;

/**
 * Created. There, you have it.
 */
public interface NodeBuilder<T extends DomainElement> {

    T buildNode();
    String id();
}
