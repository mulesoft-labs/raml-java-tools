package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;

/**
 * Created. There, you have it.
 */
public interface FilterCallBack {

    boolean filter(NamedElementPath filterPath);
}
