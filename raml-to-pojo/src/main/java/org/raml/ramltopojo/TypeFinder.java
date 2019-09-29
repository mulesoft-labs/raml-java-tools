package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import webapi.WebApiDocument;

/**
 * Created. There, you have it.
 */
public interface TypeFinder {

    Iterable<AnyShape> findTypes(WebApiDocument api);
}
