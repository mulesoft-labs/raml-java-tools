package org.raml.ramltopojo;

import amf.client.model.document.Module;
import amf.client.model.domain.*;
import webapi.WebApiDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created. There, you have it.
 */
public class TypeFindingUtils {
    static Stream<AnyShape> shapesFromTypes(WebApiDocument api) {
        return api.declares().stream().filter(x -> x instanceof AnyShape).map(x -> (AnyShape) x);
    }

    static Stream<AnyShape> shapesFromLibraries(WebApiDocument api) {
        return api.references().stream()
                .filter(x -> x instanceof Module)
                .map(x -> (Module) x)
                .flatMap(TypeFindingUtils::gettingSubModules)
                .flatMap(m -> m.declares().stream())
                .filter(x -> x instanceof AnyShape)
                .map(x -> (AnyShape) x);
    }

    static Stream<Module> gettingSubModules(Module module) {
        return Stream.concat(
                Stream.of(module),
                module.references().stream().filter(x -> x instanceof Module)
                .map(x -> (Module) x).flatMap(TypeFindingUtils::gettingSubModules));
    }

    static Stream<AnyShape> shapesFromResources(List<EndPoint> endPoints) {

        List<AnyShape> declarations = new ArrayList<>();
        for (EndPoint endPoint : endPoints) {

            // todo subresources
            //  resourceTypes(endPoint.());
            declarations.addAll(endPoint.parameters().stream().map(Parameter::schema).filter(x -> x instanceof AnyShape).map(x -> (AnyShape)x).collect(Collectors.toList()));

            for (Operation method : endPoint.operations()) {

                List<AnyShape> requestShapes = typesInRequests(endPoint, method, new ArrayList<>()).collect(Collectors.toList());
                declarations.addAll(requestShapes);
            }
        }

        return declarations.stream();
    }

    private static Stream<AnyShape> typesInRequests(EndPoint resource, Operation method, List<AnyShape> body) {

        List<AnyShape> declarations = new ArrayList<>(body);

        //declarations.addAll(method.queryParameters());

        for (Response response : method.responses()) {
            declarations.addAll(response.headers().stream().map(Parameter::schema).filter(x -> x instanceof AnyShape).map(x -> (AnyShape)x).collect(Collectors.toList()));
            declarations.addAll(response.payloads().stream().map(Payload::schema).filter(x -> x instanceof AnyShape).map(x -> (AnyShape)x).collect(Collectors.toList()));
        }

        declarations.addAll(method.request().payloads().stream().map(Payload::schema).filter(x -> x instanceof AnyShape).map(x -> (AnyShape)x).collect(Collectors.toList()));

        return declarations.stream();
    }
}
