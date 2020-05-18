package org.raml.ramltopojo.extensions;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.PropertyShape;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.CreationResult;

import java.util.Set;

/**
 * Created. There, you have it.
 */
public interface ObjectPluginContext {

    Set<TypeName> childClasses(String ramlTypeName);
    CreationResult creationResult();
    CreationResult dependentType(AnyShape items);
    TypeName forProperty(PropertyShape typeDeclaration);

    TypeName createSupportClass(TypeSpec.Builder newSupportType);
}
