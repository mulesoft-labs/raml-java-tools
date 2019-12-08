package org.raml.ramltopojo;

import amf.client.model.domain.*;
import org.apache.jena.ext.com.google.common.collect.Streams;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class Utils {

    public static Class<?> declarationType(AnyShape typeDeclaration) {

        return typeDeclaration.getClass();
    }

    static List<TypeDeclaration> goThroughLibraries(List<TypeDeclaration> foundTypes, Set<String> visitedLibraries, List<Library> libraries) {


        for (Library library : libraries) {
            if (visitedLibraries.contains(library.name())) {

                continue;
            } else {

                visitedLibraries.add(library.name());
            }

            goThroughLibraries(foundTypes, visitedLibraries, library.uses());

            foundTypes.addAll(library.types());
        }

        return foundTypes;
    }

    static public List<Shape> allParents(Shape target) {

        return allParents(target, new ArrayList<>());
    }

    static private List<Shape> allParents(Shape target, List<Shape> found) {

        found.add(target);
        for (Shape typeDeclaration : target.inherits()) {
            allParents(typeDeclaration, found);
        }

        return found;
    }

    static public String nameOf(AnyShape anyShape) {

        if ( anyShape.linkTarget().isPresent() ) {

            AnyShape shape = (AnyShape) anyShape.linkTarget().get();
            return shape.name().value();
        } else {

            return anyShape.name().value();
        }
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

    public static List<PropertyShape> allProperties(NodeShape objectTypeDeclaration) {

        return Streams.concat(
                objectTypeDeclaration.properties().stream(),
                objectTypeDeclaration.inherits().stream()
                        .flatMap(x -> ((NodeShape)x.linkTarget().orElse(x)).properties().stream())).collect(Collectors.toList());
    }
}
