package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;

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
        if ( shape.getClass().equals(AnyShape.class) && shape.inherits().size() == 1) {
            return (AnyShape) shape.inherits().get(0);
        }
        return (AnyShape) propertyShape.range();
    }

    static public AnyShape items(ArrayShape shape) {

        return (AnyShape) Optional.ofNullable(shape.items()).orElseGet(() -> itemsFromInheritance(shape));
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
