package org.raml.builder;

import amf.client.model.domain.FileShape;

/**
 * Created. There, you have it.
 */
public class FileShapeBuilder extends TypeShapeBuilder<FileShape, FileShapeBuilder> {

    @Override
    protected FileShape buildNodeLocally() {
        return new FileShape();
    }

    @Override
    public FileShape buildReferenceShape() {

        return new FileShape();
    }
}
