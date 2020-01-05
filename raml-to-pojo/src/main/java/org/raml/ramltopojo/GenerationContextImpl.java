package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.AnyShape;
import amf.client.model.domain.Shape;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.extensions.*;
import org.raml.ramltopojo.plugin.PluginManager;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created. There, you have it.
 */
public class GenerationContextImpl implements GenerationContext {

    private final PluginManager pluginManager;
    private final Document api;
    private final TypeFetcher typeFetcher;
    private final ConcurrentHashMap<String, CreationResult> knownTypes = new ConcurrentHashMap<>();
    private final SetMultimap<String, String> childTypes = HashMultimap.create();
    private final String defaultPackage;
    private final List<String> basePlugins;
    private Map<String, TypeSpec> supportClasses = new HashMap<>();

    public GenerationContextImpl(Document api) {
        this(PluginManager.NULL, api, TypeFetchers.NULL_FETCHER, "", Collections.<String>emptyList());
    }

    public GenerationContextImpl(PluginManager pluginManager, Document api, TypeFetcher typeFetcher, String defaultPackage, List<String> basePlugins) {
        this.pluginManager = pluginManager;
        this.api = api;
        this.typeFetcher = typeFetcher;
        this.defaultPackage = defaultPackage;
        this.basePlugins = basePlugins;
    }

    @Override
    public CreationResult findCreatedType(String typeName, Shape ramlType) {


        if ( knownTypes.containsKey(typeName) ) {

            return knownTypes.get(typeName);
        } else {

            Shape typeDeclaration = typeFetcher.fetchType(api, typeName);
            Optional<CreationResult> result =  CreationResultFactory.createType((AnyShape) typeDeclaration, this);

            // todo fix this.
            if ( result.isPresent() ) {
                knownTypes.put(typeName, result.get());
                return result.get();
            }  else {
                return null;
            }
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

    public void setupTypeHierarchy(String actualName, AnyShape typeDeclaration) {

        List<TypeDeclaration> parents = typeDeclaration.parentTypes();
        for (AnyShape parent : parents) {
            setupTypeHierarchy(parent.name(), parent);
            if ( ! parent.name().value().equals(actualName) ) {
                childTypes.put(parent.name(), actualName);
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
    public void createSupportTypes(String rootDirectory) throws IOException {
        for (TypeSpec typeSpec : supportClasses.values()) {

            JavaFile.builder(defaultPackage(), typeSpec).build().writeTo(Paths.get(rootDirectory));
        }
    }

    private<T> void loadBasePlugins(Set<T> plugins, Class<T> pluginType, Shape... typeDeclarations) {

        for (String basePlugin : basePlugins) {
            plugins.addAll(pluginManager.getClassesForName(basePlugin, Collections.<String>emptyList(), pluginType));
        }
    }

    @Override
    public TypeName createSupportClass(TypeSpec.Builder newSupportType) {


        TypeSpec typeSpec = newSupportType.build();
        if ( supportClasses.containsKey(typeSpec.name) ) {

            TypeSpec builder = supportClasses.get(typeSpec.name);
            return ClassName.get(this.defaultPackage, builder.name);
        } else {

            this.supportClasses.put(typeSpec.name, typeSpec);
            return ClassName.get(this.defaultPackage, typeSpec.name);
        }
    }

    @Override
    public ObjectTypeHandlerPlugin pluginsForObjects(Shape... typeDeclarations) {

        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        //System.err.println("annotation defined plugins for " + typeDeclarations[0].name() + "are " + data);
        Set<ObjectTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, ObjectTypeHandlerPlugin.class);
        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , ObjectTypeHandlerPlugin.class));
        }
        //System.err.println("plugin definitions for object type " + plugins + " for " + typeDeclarations[0].name());
        return new ObjectTypeHandlerPlugin.Composite(plugins);
    }


    @Override
    public EnumerationTypeHandlerPlugin pluginsForEnumerations(Shape... typeDeclarations) {
        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<EnumerationTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, EnumerationTypeHandlerPlugin.class);

        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , EnumerationTypeHandlerPlugin.class));
        }
        return new EnumerationTypeHandlerPlugin.Composite(plugins);
    }

    @Override
    public ArrayTypeHandlerPlugin pluginsForArrays(Shape... typeDeclarations) {
        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<ArrayTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, ArrayTypeHandlerPlugin.class);

        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , ArrayTypeHandlerPlugin.class));
        }
        return new ArrayTypeHandlerPlugin.Composite(plugins);
    }

    @Override
    public UnionTypeHandlerPlugin pluginsForUnions(Shape... typeDeclarations) {
        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<UnionTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, UnionTypeHandlerPlugin.class);
        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , UnionTypeHandlerPlugin.class));
        }
        return new UnionTypeHandlerPlugin.Composite(plugins);
    }

    @Override
    public ReferenceTypeHandlerPlugin pluginsForReferences(Shape... typeDeclarations) {

        List<PluginDef> data = Annotations.PLUGINS.get(Collections.<PluginDef>emptyList(), api, typeDeclarations);
        Set<ReferenceTypeHandlerPlugin> plugins = new HashSet<>();
        loadBasePlugins(plugins, ReferenceTypeHandlerPlugin.class);
        for (PluginDef datum : data) {
            plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments() , ReferenceTypeHandlerPlugin.class));
        }
        return new ReferenceTypeHandlerPlugin.Composite(plugins);
    }


    @Override
    public Document api() {
        return api;
    }
}
