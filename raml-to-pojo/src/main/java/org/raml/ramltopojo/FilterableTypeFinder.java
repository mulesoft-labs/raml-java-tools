package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.*;

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

        WebApi encoded = (WebApi) api.encodes();
        for (EndPoint endPoint : encoded.endPoints()) {

            for (Parameter p : endPoint.parameters()) {
                foundCallback.found(namedElementPath.append(endPoint, p), (AnyShape) p.schema());
            }

            for ( Operation operation : endPoint.operations()) {

                Request request = operation.request();
                for (Parameter p : request.queryParameters()) {
                    foundCallback.found(namedElementPath.append(endPoint, operation, p), (AnyShape) p.schema());
                }

                for (Parameter p : request.headers()) {
                    foundCallback.found(namedElementPath.append(endPoint, operation, p), (AnyShape) p.schema());
                }

                for ( Response response: operation.responses()) {

                    for (Parameter p : response.headers()) {
                        foundCallback.found(namedElementPath.append(endPoint, operation, response, p), (AnyShape) p.schema());
                    }

                    for (Payload payload: response.payloads()) {
                        foundCallback.found(namedElementPath.append(endPoint, operation, response, payload), (AnyShape) payload.schema());
                    }
                }
            }
        }


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
