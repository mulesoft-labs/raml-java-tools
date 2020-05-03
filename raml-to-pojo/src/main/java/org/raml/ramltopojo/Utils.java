package org.raml.ramltopojo;

import amf.client.model.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class Utils {

    public static Class<?> declarationType(AnyShape typeDeclaration) {

        return typeDeclaration.getClass();
    }

    static public List<AnyShape> allParents(AnyShape target) {

        return allParents(target, new ArrayList<>());
    }

    static private List<AnyShape> allParents(AnyShape target, List<AnyShape> found) {

        found.add(target);
        for (Shape typeDeclaration : target.inherits()) {
            allParents((AnyShape) typeDeclaration, found);
        }

        return found;
    }

    static public AnyShape rangeOf(PropertyShape propertyShape) {

        Shape shape = propertyShape.range();
        if ( shape instanceof RecursiveShape) {
            RecursiveShape rs = (RecursiveShape) shape;
            amf.core.model.domain.RecursiveShape recursiveDomain = rs._internal();
            amf.core.model.domain.Shape deepShape = recursiveDomain.fixpointTarget().get();
            return new NodeShape((amf.plugins.domain.shapes.models.NodeShape) deepShape);
        } else {
            if (shape.getClass().equals(AnyShape.class) && shape.inherits().size() == 1) {
                return (AnyShape) shape.inherits().get(0);
            }
            return (AnyShape) propertyShape.range();
        }
    }

    static public AnyShape items(ArrayShape shape) {

        // todo make this a bit better.
        Shape items = shape.items();
        if ( items instanceof RecursiveShape) {
            RecursiveShape rs = (RecursiveShape) items;
            amf.core.model.domain.RecursiveShape recursiveDomain = rs._internal();
            amf.core.model.domain.Shape deepShape = recursiveDomain.fixpointTarget().get();
            return new NodeShape((amf.plugins.domain.shapes.models.NodeShape) deepShape);
        } else {

            return (AnyShape) Optional.ofNullable(shape.items()).orElseGet(() -> itemsFromInheritance(shape));
        }
    }

    private static Shape itemsFromInheritance(ArrayShape shape) {
        if ( shape.linkTarget().isPresent()) {

            return (Shape) shape.linkTarget().get();
        }

        if ( shape.inherits().isEmpty()) {
            return null;
        }
        return ((ArrayShape)shape.inherits().get(0)).items();
    }

}
