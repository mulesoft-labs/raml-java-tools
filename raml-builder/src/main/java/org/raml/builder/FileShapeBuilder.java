package org.raml.builder;

import amf.client.model.domain.FileShape;

/**
 * Created. There, you have it.
 */
public class FileShapeBuilder extends TypeShapeBuilder<FileShape, FileShapeBuilder> {

    public FileShapeBuilder() {
    }

    @Override
    protected FileShape buildNodeLocally() {
        return new FileShape();
    }


    @Override
    public FileShape buildReferenceShape() {

        FileShape shape = new FileShape();
        shape.withName("file");
        return shape;
    }
}
