package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.document.Module;
import amf.client.model.domain.*;
import java.util.stream.Stream;


/**
 * Created. There, you have it.
 */
public class FilterableTypeFinder {

    public void findTypes(Document api, FilterCallBack filterCallBack, FoundCallback foundCallback) {

        findTypes(
                api.declares().stream().filter(p -> p instanceof AnyShape).map(s -> (AnyShape)s),
                ((WebApi)api.encodes()).endPoints().stream(),
                api.references().stream()
                        .filter(x -> x instanceof Module)
                        .map(x -> (Module) x),
                NamedElementPath.root(), filterCallBack, foundCallback);
    }

    private void findTypes(Stream<AnyShape> supplierOfDeclarations, Stream<EndPoint> supplierOfEncodings, Stream<Module> supplierOfModules, NamedElementPath namedElementPath, FilterCallBack filterCallBack, FoundCallback foundCallback) {

        supplierOfDeclarations
                .filter(s -> filterCallBack.filter(namedElementPath))
                .forEach(s -> foundCallback.found(namedElementPath.append(s), s));

        supplierOfEncodings.forEach( endPoint -> {

            NamedElementPath epPath = namedElementPath.append(endPoint);
            for (Parameter p : endPoint.parameters()) {
                foundCallback.found(epPath.append( p).append(p.schema()), (AnyShape) p.schema());
            }

            for ( Operation operation : endPoint.operations()) {

                NamedElementPath opPath = epPath.append(operation);

                Request request = operation.request();
                for (Parameter p : request.queryParameters()) {
                    foundCallback.found(opPath.append(p).append(p.schema()), (AnyShape) p.schema());
                }

                for (Parameter p : request.headers()) {
                    foundCallback.found(opPath.append(p).append(p.schema()), (AnyShape) p.schema());
                }

                for (Payload payload: request.payloads()) {
                    foundCallback.found(opPath.append(payload).append(payload.schema()), (AnyShape) payload.schema());
                }

                for ( Response response: operation.responses()) {

                    NamedElementPath respPath = opPath.append(response);

                    for (Parameter p : response.headers()) {
                        foundCallback.found(respPath.append(p).append(p.schema()), (AnyShape) p.schema());
                    }

                    for (Payload payload: response.payloads()) {
                        foundCallback.found(respPath.append(payload).append(payload.schema()), (AnyShape) payload.schema());
                    }
                }
            }
        });


        supplierOfModules.forEach(m -> findTypes(
                m.declares().stream().filter(p -> p instanceof AnyShape).map(s -> (AnyShape)s),
                Stream.empty(),
                m.references().stream()
                        .filter(x -> x instanceof Module)
                        .map(x -> (Module) x), namedElementPath.append(m), filterCallBack, foundCallback)
                );
    }

}
