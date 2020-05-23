package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;

/**
 * Created. There, you have it.
 */
public interface TypeCreator {

    TypeName newTypeDeclaration(String name, AnyShape shape);
}
