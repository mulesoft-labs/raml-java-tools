package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
@AllArgsConstructor
public class NamedType {

    @Getter
    private final AnyShape shape;

    public Optional<TypeName> name() {
        return Optional.ofNullable(name);
    }

    private TypeName name;

    public void nameType(TypeName name) {

        this.name = name;
    }
}
