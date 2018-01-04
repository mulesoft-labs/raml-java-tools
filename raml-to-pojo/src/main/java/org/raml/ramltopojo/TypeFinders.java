package org.raml.ramltopojo;

import com.google.common.collect.FluentIterable;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

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

    public static TypeFinder everyWhere() {

        return new TypeFinder() {
            @Override
            public Iterable<TypeDeclaration> findTypes(Api api) {

                return FluentIterable.from(api.types())
                        .append(resourceTypes(api.resources()))
                        .append(Utils.goThroughLibraries(new ArrayList<TypeDeclaration>(), new HashSet<String>(), api.uses()));
            }
        };
    }

    public static TypeFinder inResources() {

        return new TypeFinder() {
            @Override
            public Iterable<TypeDeclaration> findTypes(Api api) {
                return resourceTypes(api.resources());
            }
        };
    }

    private static List<TypeDeclaration> resourceTypes(List<Resource> resources) {

        List<TypeDeclaration> declarations = new ArrayList<>();
        for (Resource resource : resources) {

            resourceTypes(resource.resources());
            declarations.addAll(resource.uriParameters());

            for (Method method : resource.methods()) {

                List<TypeDeclaration> methodDeclarations = typesInBodies(resource, method, method.body());
                declarations.addAll(methodDeclarations);
            }
        }

        return declarations;
    }

    private static List<TypeDeclaration> typesInBodies(Resource resource, Method method, List<TypeDeclaration> body) {

        List<TypeDeclaration> declarations = new ArrayList<>();

        declarations.addAll(body);

        declarations.addAll(method.queryParameters());

        declarations.addAll(method.headers());

        for (Response response : method.responses()) {
            declarations.addAll(response.body());
        }

        return declarations;
    }


}
