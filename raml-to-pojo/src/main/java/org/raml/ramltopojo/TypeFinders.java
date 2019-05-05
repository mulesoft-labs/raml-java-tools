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
public class TypeFinders {

    public static TypeFinder inTypes() {

        return api -> api.declares().stream().filter(x -> x instanceof Shape).map(x -> (Shape) x).collect(Collectors.toList());
    }

    public static TypeFinder inLibraries() {

        return (api) -> shapesFromLibraries(api)
                .collect(Collectors.toList()); // need to figure this out.
    }

    public static TypeFinder everyWhere() {

        return (api) -> Stream.concat(
                Stream.concat(
                        shapesFromTypes(api),
                        shapesFromResources(((WebApi) api.encodes()).endPoints())
                ), shapesFromLibraries(api)
        ).collect(Collectors.toList());
    }

    public static TypeFinder inResources() {

        return (api) -> shapesFromResources(((WebApi) api.encodes()).endPoints()).collect(Collectors.toList());
    }


    private static Stream<Shape> shapesFromTypes(WebApiDocument api) {
        return api.declares().stream().filter(x -> x instanceof Shape).map(x -> (Shape) x);
    }


    private static Stream<Shape> shapesFromLibraries(WebApiDocument api) {
        return api.references().stream()
                .filter(x -> x instanceof Module)
                .map(x -> (Module) x)
                .flatMap(x -> x.declares().stream())
                .filter(x -> x instanceof Shape)
                .map(x -> (Shape) x);
    }


    private static Stream<Shape> shapesFromResources(List<EndPoint> endPoints) {

        List<Shape> declarations = new ArrayList<>();
        for (EndPoint endPoint : endPoints) {

            // todo subresources
            //  resourceTypes(endPoint.());
            declarations.addAll(endPoint.parameters().stream().map(Parameter::schema).collect(Collectors.toList()));

            for (Operation method : endPoint.operations()) {

                List<Shape> requestShapes = typesInRequests(endPoint, method, new ArrayList<>());
                declarations.addAll(requestShapes);
            }
        }

        return declarations.stream();
    }

    private static List<Shape> typesInRequests(EndPoint resource, Operation method, List<Shape> body) {

        List<Shape> declarations = new ArrayList<>(body);

        //declarations.addAll(method.queryParameters());

        for (Response response : method.responses()) {
            declarations.addAll(response.headers().stream().map(Parameter::schema).collect(Collectors.toList()));
            declarations.addAll(response.payloads().stream().map(Payload::schema).collect(Collectors.toList()));
        }

        declarations.addAll(method.request().payloads().stream().map(Payload::schema).collect(Collectors.toList()));

        return declarations;
    }


}
