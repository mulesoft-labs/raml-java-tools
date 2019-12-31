package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.Shape;

/**
 * Created. There, you have it.
 */
public interface TypeFetcher {

    Shape fetchType(Document api, String name) throws GenerationException;
}
