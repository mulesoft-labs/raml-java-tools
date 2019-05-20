package org.raml.ramltopojo;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.TypeName;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public interface RamlToPojo {

    ResultingPojos buildPojos();
    ResultingPojos buildPojo(Shape typeDeclaration);
    ResultingPojos buildPojo(String suggestedJavaName, TypeDeclaration typeDeclaration);

    TypeName fetchType(String suggestedName, TypeDeclaration typeDeclaration);
    boolean isInline(TypeDeclaration typeDeclaration);
}
