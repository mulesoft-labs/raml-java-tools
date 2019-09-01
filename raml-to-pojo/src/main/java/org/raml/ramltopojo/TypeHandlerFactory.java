package org.raml.ramltopojo;

import amf.client.model.domain.Shape;

/**
 * Created. There, you have it.
 */
public interface TypeHandlerFactory {

    TypeHandler createHandler(String name, ShapeType type, Shape typeDeclaration);
}
