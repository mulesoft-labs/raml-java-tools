# raml-to-pojo

The simples way to use this code:
```java
public class Main {

    public static void main(String[] args) throws Exception  {

        final Api api = ramlModelResult.getApiV10();
        RamlToPojo ramlToPojo = RamlToPojoBuilder.builder(api)
                .inPackage("my.package")
                .fetchTypes(fromAnywhere())
                .findTypes(everyWhere()).build();

        ramlToPojo.buildPojos().createAllTypes("src/main/java");
    }
}
```

The type finder specifies which classes you are trying to generate, and the type fetcher specifies how to find transitive 
types. You must also specify the package where the classes will be generated. This gives you a RamlToPojo object.

From that object, you can create the scanned types in a target directory.  You can either create all the types seen by RamlToPojo,
or only create the types that were found (not fetched) into a target directory.  

# Changing the generated code

The generated code can be changed by adding plugins to the pipeline.  The plugins can be added two different ways:

* By adding the plugins to the RamlToPojo builder configuration
```java
public class Main {

    public static void main(String[] args) throws Exception  {

        final Api api = ramlModelResult.getApiV10();
        RamlToPojo ramlToPojo = RamlToPojoBuilder.builder(api).build("plugin1", "plugin2")
                .findTypes(everyWhere()).build();
    }
}
```
* By annotating your RAML 1.0 file to activate a plugin.
```yaml
#%RAML 1.0
title: Hello World API
version: v1
baseUri: https://api.github.com
uses:
  ramltopojo: ramltopojo.raml
(ramltopojo.types):
    plugins:
      - name: core.splitInterface
        arguments: [com.fun, com.fun.impl]
```
Annotations can also be added directly on types, or properties.

Current supported plugins are:
* core.jsr303: add jsr303 annotation to types.
* core.jackson2: add jackson2 annotations to types.
* core.jackson:  same as jackson2, for backward compatibility
* core.jaxb: add jaxb annotations.
* core.gson: add extremely minimal GSON annotations
* core.rename:  rename the given type (one argument, the new name)
* core.javadoc: add javadoc to the types, using the RAML description.
* core.repackage: change the package for the given type (one argument, the new package name)
* core.splitInterface:  separate interface and implementation into different packages
* core.changeType: change the generated type (two arguments:  the new type, and the optional "unbox" argument, to unbox java primitives)
* core.box: box java primitive types.
* core.boxWhenNotRequired: box primitive types when not required.

# writing your own plugin

The simplest way to do this is to package a jar with a META-INF/ramltopojo-plugin.properties file listing your plugins, as in 
this [example](src/main/resources/META-INF/ramltopojo-plugin.properties).  There are four interfaces that you might have to 
implement.

* [ObjectTypeHandlerPlugin](src/main/java/org/raml/ramltopojo/extensions/ObjectTypeHandlerPlugin.java) handles RAML object types.
* [UnionTypeHandlerPlugin](src/main/java/org/raml/ramltopojo/extensions/UnionTypeHandlerPlugin.java) handles RAML union types types.
* [EnumerationTypeHandlerPlugin](src/main/java/org/raml/ramltopojo/extensions/EnumerationTypeHandlerPlugin.java) handles RAML enum types.
* [ReferenceTypeHandlerPlugin](src/main/java/org/raml/ramltopojo/extensions/ReferenceTypeHandlerPlugin.java) handles RAML reference types (strings and such).

You may implement these interfaces in separate classes and combine these classes as a list (like the core.jackson2 plugin), 
or all the interfaces in one class (like pretty much all the others).  You may want to read up on [JavaPoet](https://github.com/square/javapoet)
