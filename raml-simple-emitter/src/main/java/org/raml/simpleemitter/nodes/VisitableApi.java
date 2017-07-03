package org.raml.simpleemitter.nodes;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.parsertools.Augmenter;
import org.raml.simpleemitter.ApiVisitor;
import org.raml.simpleemitter.Visitable;
import org.raml.simpleemitter.api.ModifiableApi;
import org.raml.simpleemitter.api.ModifiableResource;
import org.raml.simpleemitter.api.ModifiableTypeDeclaration;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class VisitableApi extends Helper implements Visitable {
    final private Api api;

    public VisitableApi(Api api) {
        this.api = api;
    }

    @Override
    public void visit(ApiVisitor v) {

        ModifiableApi newApi = Augmenter.augment(ModifiableApi.class, api);

        v.visit(newApi);
        for (Resource resource : newApi.resources()) {

            ((ModifiableResource)resource).visit(v);
        }
    }

    public List<Resource> resources() {

        return toModifiable(api.resources(), ModifiableResource.class);
    }

    public List<TypeDeclaration> types() {

        return toModifiable(api.types(), ModifiableTypeDeclaration.class);
    }
}
