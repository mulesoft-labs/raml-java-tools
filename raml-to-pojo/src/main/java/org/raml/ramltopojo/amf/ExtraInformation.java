package org.raml.ramltopojo.amf;

import amf.client.model.domain.Shape;

import java.util.List;

/**
 * Created. There, you have it.
 */
public interface ExtraInformation {

    public static ExtraInformation extraInformation() {
        return new ExtraInformation() {
            @Override
            public String oldId(Shape shape) {
                return ExtraInformationImpl.oldId(shape);
            }

            @Override
            public boolean isInline(Shape shape) {
                return ExtraInformationImpl.isInline(shape);
            }

            @Override
            public List<String> parentTypes(Shape shape) {
                return ExtraInformationImpl.parentTypes(shape);
            }
        };
    }

    String oldId(Shape shape);
    boolean isInline(Shape shape);
    List<String> parentTypes(Shape shape);
}
