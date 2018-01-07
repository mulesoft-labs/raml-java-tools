package org.raml.ramltopojo;

import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class Utils {

    public static Class<?> declarationType(TypeDeclaration typeDeclaration) {

        return typeDeclaration.getClass().getInterfaces()[0];
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

    static public  List<TypeDeclaration> allParents(TypeDeclaration target, List<TypeDeclaration> found) {

        found.add(target);
        for (TypeDeclaration typeDeclaration : target.parentTypes()) {
            allParents(typeDeclaration, found);
        }

        return found;
    }
}
