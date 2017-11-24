package org.raml.ramltopojo;

import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class TypeFinders {

    public static TypeFinder inTypes() {

        return new TypeFinder() {
            @Override
            public Iterable<TypeDeclaration> findTypes(Api api) {

                return api.types();
            }
        };
    }

    public static TypeFinder inLibraries() {

        return new TypeFinder() {
            @Override
            public Iterable<TypeDeclaration> findTypes(Api api) {

                List<TypeDeclaration> foundTypes = new ArrayList<>();
                goThroughLibraries(foundTypes, new HashSet<String>(), api.uses());
                return foundTypes;
            }
        };
    }

    public static  TypeFinder everyWhere() {

        return new TypeFinder() {
            @Override
            public Iterable<TypeDeclaration> findTypes(Api api) {

                return FluentIterable.from(api.types()).append(goThroughLibraries(new ArrayList<TypeDeclaration>(), new HashSet<String>(), api.uses()));
            }
        };
    }
    private static List<TypeDeclaration> goThroughLibraries(List<TypeDeclaration> foundTypes, Set<String> visitedLibraries, List<Library> libraries) {


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

}
