package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.AnyShape;

/**
 * Created. There, you have it.
 */
public interface TypeFinder {

    Iterable<AnyShape> findTypes(Document api);
}
