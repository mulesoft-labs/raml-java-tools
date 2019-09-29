package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;
import java.util.Set;

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

    static public List<Shape> allParents(Shape target, List<Shape> found) {

        found.add(target);
        for (Shape typeDeclaration : target.inherits()) {
            allParents(typeDeclaration, found);
        }

        return found;
    }

    static public AnyShape rangeOf(PropertyShape propertyShape) {

        return (AnyShape) propertyShape.range();
    }
}
