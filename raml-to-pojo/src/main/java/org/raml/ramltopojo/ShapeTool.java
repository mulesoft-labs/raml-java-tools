package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import lombok.RequiredArgsConstructor;
import org.raml.ramltopojo.amf.ExtraInformation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
@RequiredArgsConstructor
public class ShapeTool {

    private final GenerationContextImpl context;
    private final ExtraInformation info;

    public boolean isInline(AnyShape shape) {

        return info.isInline(shape);
    }

    public List<AnyShape> parentShapes(AnyShape shape) {

        return info.parentTypes(shape).stream()
                .map(n -> context.findShapeById(n).orElseThrow(() -> new GenerationException("couldn't find type with id " + n)))
                .collect(Collectors.toList());
    }
}
