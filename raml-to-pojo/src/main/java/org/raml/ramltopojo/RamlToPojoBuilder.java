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
    private TypeFetcher typeFetcher;
    private TypeFinder typeFinder;

    public RamlToPojoBuilder(Document api) {

        this.api = api;
    }

    public static RamlToPojoBuilder builder(Document api) {

        return new RamlToPojoBuilder(api);
    }

    public RamlToPojoBuilder inPackage(String packageName) {

        this.packageName = packageName;
        return this;
    }

    public RamlToPojoBuilder fetchTypes(TypeFetcher typeFetcher) {

        this.typeFetcher = typeFetcher;
        return this;
    }

    public RamlToPojoBuilder findTypes(TypeFinder typeFinder) {

        this.typeFinder = typeFinder;
        return this;
    }

    public RamlToPojo build() {

        return build(Collections.<String>emptyList());
    }

    public RamlToPojo build(List<String> basePlugins) {

        return new RamlToPojoImpl(typeFinder, new GenerationContextImpl(PluginManager.createPluginManager(), api, typeFetcher, packageName, basePlugins));
    }


}
