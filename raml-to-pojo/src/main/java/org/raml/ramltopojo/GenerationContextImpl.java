package org.raml.ramltopojo;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.ClassName;
import org.raml.ramltopojo.extensions.EnumerationTypeHandlerPlugin;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.ramltopojo.extensions.ReferenceTypeHandlerPlugin;
import org.raml.ramltopojo.extensions.UnionTypeHandlerPlugin;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.IOException;
import java.util.Collections;
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
    private final List<String> basePlugins;

    public GenerationContextImpl(Api api) {
        this(PluginManager.NULL, api, TypeFetchers.NULL_FETCHER, "", Collections.<String>emptyList());
    }

    public GenerationContextImpl(PluginManager pluginManager, Api api, TypeFetcher typeFetcher, String defaultPackage, List<String> basePlugins) {
        this.pluginManager = pluginManager;
        this.api = api;
        this.typeFetcher = typeFetcher;
        this.defaultPackage = defaultPackage;
        this.basePlugins = basePlugins;
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

    @Override
    public ClassName buildDefaultClassName(String name, EventType eventType) {
        return ClassName.get(defaultPackage, name);
    }

    public void setupTypeHierarchy(TypeDeclaration typeDeclaration) {

        List<TypeDeclaration> parents = typeDeclaration.parentTypes();
        for (TypeDeclaration parent : parents) {
            setupTypeHierarchy(parent);
            childTypes.put(parent.name(), typeDeclaration.name());
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

    private<T> void loadBasePlugins(Set<T> plugins, Class<T> pluginType, TypeDeclaration... typeDeclarations) {

        for (TypeDeclaration typeDeclaration : typeDeclarations) {

            if ( Annotations.CLASS_NAME.get(typeDeclaration) != null) {

                pluginManager.getClassesForName("core.rename", Collections.singletonList(Annotations.CLASS_NAME.get(typeDeclaration)), pluginType);
            }

            if ( Annotations.IMPLEMENTATION_CLASS_NAME.get(typeDeclaration) != null) {

                pluginManager.getClassesForName("core.renameImplementation", Collections.singletonList(Annotations.IMPLEMENTATION_CLASS_NAME.get(typeDeclaration)), pluginType);
            }

            if (!Annotations.USE_PRIMITIVE.get(typeDeclaration)) {

                pluginManager.getClassesForName("core.box", Collections.<String>emptyList(), pluginType);
            }

            if (!Annotations.ABSTRACT.get(typeDeclaration)) {

                pluginManager.getClassesForName("core.makeAbstract", Collections.<String>emptyList(), pluginType);
            }

        }
        for (String basePlugin : basePlugins) {
            plugins.addAll(pluginManager.getClassesForName(basePlugin, Collections.<String>emptyList(), pluginType));
        }
    }

    @Override
    public ObjectTypeHandlerPlugin pluginsForObjects(TypeDeclaration... typeDeclarations) {

        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<ObjectTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, ObjectTypeHandlerPlugin.class);
        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , ObjectTypeHandlerPlugin.class));
        }
        return new ObjectTypeHandlerPlugin.Composite(plugins);
    }


    @Override
    public EnumerationTypeHandlerPlugin pluginsForEnumerations(TypeDeclaration... typeDeclarations) {
        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<EnumerationTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, EnumerationTypeHandlerPlugin.class);

        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , EnumerationTypeHandlerPlugin.class));
        }
        return new EnumerationTypeHandlerPlugin.Composite(plugins);
    }

    @Override
    public UnionTypeHandlerPlugin pluginsForUnions(TypeDeclaration... typeDeclarations) {
        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<UnionTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, UnionTypeHandlerPlugin.class);
        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , UnionTypeHandlerPlugin.class));
        }
        return new UnionTypeHandlerPlugin.Composite(plugins);
    }

    @Override
    public ReferenceTypeHandlerPlugin pluginsForReferences(TypeDeclaration... typeDeclarations) {

        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<ReferenceTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, ReferenceTypeHandlerPlugin.class);
        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , ReferenceTypeHandlerPlugin.class));
        }
        return new ReferenceTypeHandlerPlugin.Composite(plugins);
    }

    @Override
    public Api api() {
        return api;
    }
}
