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
                .forEach(s -> foundCallback.found(namedElementPath.append(s), s));

        WebApi encoded = (WebApi) api.encodes();
        for (EndPoint endPoint : encoded.endPoints()) {

            for (Parameter p : endPoint.parameters()) {
                foundCallback.found(namedElementPath.append(endPoint, p, p.schema()), (AnyShape) p.schema());
            }

            for ( Operation operation : endPoint.operations()) {

                Request request = operation.request();
                for (Parameter p : request.queryParameters()) {
                    foundCallback.found(namedElementPath.append(endPoint, operation, p, p.schema()), (AnyShape) p.schema());
                }

                for (Parameter p : request.headers()) {
                    foundCallback.found(namedElementPath.append(endPoint, operation, p, p.schema()), (AnyShape) p.schema());
                }

                for (Payload payload: request.payloads()) {
                    foundCallback.found(namedElementPath.append(endPoint, operation, payload, payload.schema()), (AnyShape) payload.schema());
                }

                for ( Response response: operation.responses()) {

                    for (Parameter p : response.headers()) {
                        foundCallback.found(namedElementPath.append(endPoint, operation, response, p, p.schema()), (AnyShape) p.schema());
                    }

                    for (Payload payload: response.payloads()) {
                        foundCallback.found(namedElementPath.append(endPoint, operation, response, payload, payload.schema()), (AnyShape) payload.schema());
                    }
                }
            }
        }
    }

}
