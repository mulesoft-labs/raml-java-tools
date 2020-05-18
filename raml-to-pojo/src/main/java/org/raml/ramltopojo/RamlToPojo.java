package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
public interface RamlToPojo {

    ResultingPojos buildPojos();
    ResultingPojos buildPojo(AnyShape typeDeclaration);


    ResultingPojos buildPojo(String suggestedJavaName, String typeId);
    TypeName attributeTypeToName(String suggestedName, AnyShape anyShape);

    Optional<TypeName> fetchTypeName(AnyShape anyShape);
}
