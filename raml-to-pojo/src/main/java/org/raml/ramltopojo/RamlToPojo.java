package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.Shape;
import com.squareup.javapoet.TypeName;

/**
 * Created. There, you have it.
 */
public interface RamlToPojo {

    ResultingPojos buildPojos();
    ResultingPojos buildPojo(Shape typeDeclaration);
    ResultingPojos buildPojo(String suggestedJavaName, Shape typeDeclaration);

    TypeName fetchType(String suggestedName, Shape typeDeclaration);
    boolean isInline(AnyShape typeDeclaration);
}
