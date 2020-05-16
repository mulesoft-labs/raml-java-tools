package org.raml.ramltopojo;

import amf.client.model.document.Document;
import org.raml.ramltopojo.plugin.PluginManager;

import java.util.Collections;
import java.util.List;

/**
 * Created. There, you have it.
 */

public class RamlToPojoBuilder {

    private final Document api;
    private String packageName = "";
    private FilterCallBack typeFilter = (x) -> true;
    private FoundCallback typeFinder = (x,y) -> {};

    public RamlToPojoBuilder(Document api) {

        this.api = api;
    }

    public RamlToPojoBuilder typeFilter(FilterCallBack filterCallBack) {
        this.typeFilter = filterCallBack;
        return this;
    }

    public RamlToPojoBuilder typeFinder(FoundCallback foundCallback) {
        this.typeFinder = foundCallback;
        return this;
    }

    public RamlToPojoBuilder inPackage(String packageName) {

        this.packageName = packageName;
        return this;
    }

    public static RamlToPojoBuilder builder(Document api) {

        return new RamlToPojoBuilder(api);
    }


    public RamlToPojo build() {

        return build(Collections.<String>emptyList());
    }

    public RamlToPojo build(List<String> basePlugins) {

        return new RamlToPojoImpl(new GenerationContextImpl(PluginManager.createPluginManager(), api, new FilterableTypeFinder(), typeFilter, typeFinder, packageName, basePlugins));
    }


}
