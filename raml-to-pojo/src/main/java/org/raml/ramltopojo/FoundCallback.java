package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;

/**
 * Created. There, you have it.
 */
public interface FoundCallback {

    void found(NamedElementPath parentPath, AnyShape shape);
}
