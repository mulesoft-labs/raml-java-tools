package org.raml.ramltopojo;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.WebApi;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created. There, you have it.
 */
public class TypeFinders {

    public static TypeFinder inTypes() {

        return api -> api.declares().stream().filter(x -> x instanceof AnyShape).map(x -> (AnyShape) x).collect(Collectors.toList());
    }

    public static TypeFinder inLibraries() {

        return (api) -> TypeFindingUtils.shapesFromLibraries(api)
                .collect(Collectors.toList()); // need to figure this out.
    }

    public static TypeFinder everyWhere() {

        return (api) -> Stream.concat(
                Stream.concat(
                        TypeFindingUtils.shapesFromTypes(api),
                        TypeFindingUtils.shapesFromResources(((WebApi) api.encodes()).endPoints())
                ), TypeFindingUtils.shapesFromLibraries(api)
        ).collect(Collectors.toList());
    }

    public static TypeFinder inResources() {

        return (api) -> TypeFindingUtils.shapesFromResources(((WebApi) api.encodes()).endPoints()).collect(Collectors.toList());
    }


}
