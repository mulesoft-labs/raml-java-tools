package org.raml.ramltopojo;

import amf.client.model.document.Document;
import amf.client.model.domain.AnyShape;
import amf.client.model.domain.NodeShape;
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
    private final ConcurrentHashMap<String, TypeName> typeNames = new ConcurrentHashMap<>();

    private final SetMultimap<String, String> childTypes = HashMultimap.create();
    private final String defaultPackage;
    private final List<String> basePlugins;
    private Map<String, TypeSpec> supportClasses = new HashMap<>();
    private Map<String, AnyShape> realTypes = new HashMap<>();

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

    public void newTypeName(String name, TypeName typeName) {
        this.typeNames.put(name, typeName);
    }

    public void setupTypeHierarchy(String actualName, AnyShape typeDeclaration) {

        if ( actualName != null && ! realTypes.containsKey(actualName) ) {
            realTypes.put(actualName, typeDeclaration);
        }
        if ( typeDeclaration instanceof NodeShape) {
            for (String parent:  ExtraInformation.parentTypes(typeDeclaration)) {

                if (parent != null && !parent.equals(actualName)) {
                    childTypes.put(parent, actualName);
                }
            }
        }
    }

    @Override
    public Optional<TypeName> findTypeNameByRamlName(String ramlName) {
        return Optional.ofNullable(typeNames.get(ramlName));
    }

    @Override
    public AnyShape findOriginalDeclaredName(String name) {
        // really stupid
        return realTypes.get(name);
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

    private <T> Set<T> appropriatePlugins(Class<T> ofType, Shape... typeDeclarations) {

        List<PluginDef> data = Annotations.PLUGINS.get(Collections.emptyList(), api, typeDeclarations);
        //System.err.println("annotation defined plugins for " + typeDeclarations[0].name() + "are " + data);
        Set<T> plugins = new HashSet<>();
        loadBasePlugins(plugins, ofType);
        for (PluginDef datum : data) {
            Set<T> classesForName = pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments(), ofType);
            for (T somePlugin : classesForName) {
                plugins.removeIf(x ->  x.getClass() == somePlugin.getClass());
            }
            plugins.addAll(classesForName);
        }

        return plugins;
    }

    @Override
    public ObjectTypeHandlerPlugin pluginsForObjects(Shape... typeDeclarations) {

        return new ObjectTypeHandlerPlugin.Composite(appropriatePlugins(ObjectTypeHandlerPlugin.class, typeDeclarations));
    }


    @Override
    public EnumerationTypeHandlerPlugin pluginsForEnumerations(Shape... typeDeclarations) {

        return new EnumerationTypeHandlerPlugin.Composite(appropriatePlugins(EnumerationTypeHandlerPlugin.class, typeDeclarations));
    }

    @Override
    public ArrayTypeHandlerPlugin pluginsForArrays(Shape... typeDeclarations) {

        return new ArrayTypeHandlerPlugin.Composite(appropriatePlugins(ArrayTypeHandlerPlugin.class, typeDeclarations));
    }

    @Override
    public UnionTypeHandlerPlugin pluginsForUnions(Shape... typeDeclarations) {

        return new UnionTypeHandlerPlugin.Composite(appropriatePlugins(UnionTypeHandlerPlugin.class, typeDeclarations));
    }

    @Override
    public ReferenceTypeHandlerPlugin pluginsForReferences(Shape... typeDeclarations) {

        return new ReferenceTypeHandlerPlugin.Composite(appropriatePlugins(ReferenceTypeHandlerPlugin.class, typeDeclarations));
    }


    @Override
    public Document api() {
        return api;
    }
}
