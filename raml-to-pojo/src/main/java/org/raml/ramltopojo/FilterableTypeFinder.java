package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.AnyShape;
import amf.client.model.domain.WebApi;

import java.util.stream.Collectors;

/**
 * Created. There, you have it.
 */
public class FilterableTypeFinder {

    public void findTypes(Document api, FilterCallBack filterCallBack, FoundCallback foundCallback) {

        findTypes(api, NamedElementPath.root(), filterCallBack, foundCallback);
    }

    private void findTypes(Document api, NamedElementPath namedElementPath, FilterCallBack filterCallBack, FoundCallback foundCallback) {

        api.declares().stream()
                .filter(x -> x instanceof AnyShape)
                .map(x -> (AnyShape) x)
                .filter(s -> filterCallBack.filter(namedElementPath))
                .forEach(s -> foundCallback.found(namedElementPath, s));

        ((WebApi) api.encodes()).endPoints().stream()
                .map(e -> e.operations())
                .flatMap(os -> os.stream())
                .map(o -> o.request())
                .map(r -> r.payloads())
                .flatMap(ps -> ps.stream())
                .map(p -> p.schema())
                .map(x -> (AnyShape) x)
                .filter(s -> filterCallBack.filter(namedElementPath))
                .forEach(s -> foundCallback.found(namedElementPath, s));
    }

}
