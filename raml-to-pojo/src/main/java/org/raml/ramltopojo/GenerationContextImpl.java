package org.raml.ramltopojo;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.raml.ramltopojo.object.ObjectTypeHandlerPlugin;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created. There, you have it.
 */
public class GenerationContextImpl implements GenerationContext {

    private final PluginManager pluginManager;
    private final Api api;
    private final TypeFetcher typeFetcher;
    private final ConcurrentHashMap<String, CreationResult> knownTypes = new ConcurrentHashMap<>();
    private final SetMultimap<String, String> childTypes = HashMultimap.create();
    private final String defaultPackage;

    public GenerationContextImpl(Api api) {
        this(PluginManager.NULL, api, TypeFetchers.NULL_FETCHER, "");
    }

    public GenerationContextImpl(PluginManager pluginManager, Api api, TypeFetcher typeFetcher, String defaultPackage) {
        this.pluginManager = pluginManager;
        this.api = api;
        this.typeFetcher = typeFetcher;
        this.defaultPackage = defaultPackage;
    }

    @Override
    public CreationResult findCreatedType(String typeName, TypeDeclaration ramlType) {

        if ( knownTypes.containsKey(typeName) ) {

            return knownTypes.get(typeName);
        } else {

            TypeDeclaration typeDeclaration = typeFetcher.fetchType(api, typeName);
            CreationResult result =  TypeDeclarationType.createType(typeDeclaration, this);

            knownTypes.put(typeName, result);
            return result;
        }
    }

    @Override
    public String defaultPackage() {
        return defaultPackage;
    }


    @Override
    public Set<String> childClasses(String ramlTypeName) {
        return childTypes.get(ramlTypeName);
    }

    public void setupTypeHierarchy(TypeDeclaration typeDeclaration) {

        // Temporary....
        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            ObjectTypeDeclaration objectTypeDeclaration = (ObjectTypeDeclaration) typeDeclaration;
            List<TypeDeclaration> parents = objectTypeDeclaration.parentTypes();
            for (TypeDeclaration parent : parents) {
                setupTypeHierarchy(parent);
                childTypes.put(parent.name(), objectTypeDeclaration.name());
            }
        }
    }

    @Override
    public void newExpectedType(String name, CreationResult creationResult) {
        knownTypes.put(name, creationResult);
    }

    @Override
    public void createTypes(String rootDirectory) throws IOException {

        for (CreationResult creationResult : knownTypes.values()) {
            creationResult.createType(rootDirectory);
        }
    }

    @Override
    public ObjectTypeHandlerPlugin pluginsForObjects(TypeDeclaration... typeDeclarations) {
        List<String> data = Annotations.PLUGINS.get(api);
        Set<ObjectTypeHandlerPlugin> plugins = new HashSet<>();
        for (String datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum, ObjectTypeHandlerPlugin.class));
        }
        return new ObjectTypeHandlerPlugin.Composite(plugins);
    }

    @Override
    public Api api() {
        return api;
    }
}
