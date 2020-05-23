package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;

/**
 * Created. There, you have it.
 */
public interface ExtendedFoundCallback {

    void found(NamedElementPath parentPath, AnyShape shape, TypeCreator typeCreator);
}
