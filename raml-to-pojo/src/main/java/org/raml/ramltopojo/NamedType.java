package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created. There, you have it.
 */
@AllArgsConstructor
public class NamedType {

    @Getter
    private final AnyShape shape;
    @Getter
    private String name;

    public void nameType(String name) {

        this.name = name;
    }


}
