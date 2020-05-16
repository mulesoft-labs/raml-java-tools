package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import com.squareup.javapoet.TypeName;

/**
 * Created. There, you have it.
 */
public interface RamlToPojo {

    ResultingPojos buildPojos();
    ResultingPojos buildPojo(AnyShape typeDeclaration);


    ResultingPojos buildPojo(String suggestedJavaName, String typeId);
    TypeName attributeTypeToName(String suggestedName, String typeId);
}
