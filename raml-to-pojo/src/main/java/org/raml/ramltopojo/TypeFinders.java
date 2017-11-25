package org.raml.ramltopojo;

import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
                Utils.goThroughLibraries(foundTypes, new HashSet<String>(), api.uses());
                return foundTypes;
            }
        };
    }

    public static  TypeFinder everyWhere() {

        return new TypeFinder() {
            @Override
            public Iterable<TypeDeclaration> findTypes(Api api) {

                return FluentIterable.from(api.types()).append(Utils.goThroughLibraries(new ArrayList<TypeDeclaration>(), new HashSet<String>(), api.uses()));
            }
        };
    }

}
