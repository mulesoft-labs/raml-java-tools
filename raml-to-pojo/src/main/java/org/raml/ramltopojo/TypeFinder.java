package org.raml.ramltopojo;

import amf.client.model.domain.Shape;
import webapi.WebApiDocument;

/**
 * Created. There, you have it.
 */
public interface TypeFinder {

    Iterable<Shape> findTypes(WebApiDocument api);
}
