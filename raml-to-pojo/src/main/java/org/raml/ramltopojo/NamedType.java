package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;
import lombok.AllArgsConstructor;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
@AllArgsConstructor
public class NamedType {

    private final AnyShape shape;
    private String ramlName;

    private TypeName name;


    public void nameType(TypeName name) {

        this.name = name;
    }

    public AnyShape shape() {
        return shape;
    }

    public Optional<TypeName> name() {
        return Optional.ofNullable(name);
    }

    public Optional<String> ramlName() {
        return Optional.ofNullable(ramlName);
    }

}
