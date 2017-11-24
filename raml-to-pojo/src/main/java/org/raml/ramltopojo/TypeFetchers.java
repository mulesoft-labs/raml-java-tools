package org.raml.ramltopojo;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class TypeFetchers {
    public static final TypeFetcher NULL_FETCHER = new TypeFetcher() {
        @Override
        public TypeDeclaration fetchType(Api api, String name) throws GenerationException {
            throw new GenerationException("null fetcher can't fetch types");
        }
    };

    public static TypeFetcher fromTypes() {

        return new TypeFetcher() {

            // this is technically invalid, as different apis might call.  Won't happen, but could.
            // make better
            Iterable<TypeDeclaration> foundInApi;
            @Override
            public TypeDeclaration fetchType(Api api, final String name) throws GenerationException {

                return FluentIterable.from(Optional.fromNullable(foundInApi).or(api.types()))
                        .firstMatch(new Predicate<TypeDeclaration>() {
                            @Override
                            public boolean apply(@Nullable TypeDeclaration input) {
                                return name.equals(input.name());
                            }
                        }).or(fail(name));
            }

        };
    }

    protected static Supplier<TypeDeclaration> fail(final String name) {
        return new Supplier<TypeDeclaration>() {
            @Override
            public TypeDeclaration get() {
                throw new GenerationException("can't fetch type named " + name);
            }
        };
    }

    public static TypeFetcher fromLibraries() {

        return new TypeFetcher() {

            Iterable<TypeDeclaration> foundInApi;

            @Override
            public TypeDeclaration fetchType(Api api, final String name) throws GenerationException {
                return FluentIterable.from(Optional.fromNullable(foundInApi).or(goThroughLibraries(new ArrayList<TypeDeclaration>(), new HashSet<String>(), api.uses())))
                        .firstMatch(new Predicate<TypeDeclaration>() {
                            @Override
                            public boolean apply(@Nullable TypeDeclaration input) {
                                return name.equals(input.name());
                            }
                        }).or(fail(name));
            }
        };
    }

    public static  TypeFetcher fromAnywhere() {

        return new TypeFetcher() {
            Iterable<TypeDeclaration> foundInApi;

            @Override
            public TypeDeclaration fetchType(Api api, final String name) throws GenerationException {
                return FluentIterable.from(Optional.fromNullable(foundInApi).or(FluentIterable.from(api.types()).append(goThroughLibraries(new ArrayList<TypeDeclaration>(), new HashSet<String>(), api.uses()))))
                        .firstMatch(new Predicate<TypeDeclaration>() {
                            @Override
                            public boolean apply(@Nullable TypeDeclaration input) {
                                return name.equals(input.name());
                            }
                        }).or(fail(name));
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
