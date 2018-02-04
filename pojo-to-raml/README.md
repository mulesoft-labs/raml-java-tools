# pojo-to-raml

The simplest way to use this code:

```java
class MyClass {
    public static void main(String[] args){
            PojoToRaml pojoToRaml = PojoToRamlBuilder.create();
            Result result = pojoToRaml.classToRaml(MyPojo.class);          
    }
}
```

This will generate the class and dependent classes as RAML 1.0 type declarations.

The default class parsing implementation introspectively looks 
in your given class and creates properties for fields and RAML superclasses from java superclasses.

If your classes are defined differently, you can create your own implementation of the class parser factory and class parser
to do simple discovery in your class.  

```java
class MyClass {
    public static void main(String[] args){
            PojoToRaml pojoToRaml = PojoToRamlBuilder.create(new MyOwnClassParserFactory(), new MyOwnAdjusterFactory());
            Result result = pojoToRaml.classToRaml(MyPojo.class);          
    }
}
```

There is an annotation driven class parser factory defined in the project, the [PojoToRamlClassParserFactory](src/main/java/org/raml/pojotoraml/plugins/PojoToRamlClassParserFactory.java)
that allows you to annotate your types individually to have different class parsers per class.  
It's used in the jaxrs-to-pojo project.

Adjusters are plugins that allow you to modify the generated raml to suit your particular needs, either by adding raml information
or by doing more radical operations such as changing the generated type (should you be using UUIDs instead of string in your Java objects)

There is an annotation driven AdjusterFactory, called the [PojoToRamlExtensionFactory](src/main/java/org/raml/pojotoraml/plugins/PojoToRamlExtensionFactory.java).
It allows you to annotate classes and packages to allow you to override the normal RAML generation.  For example, you can annotate a class to 
activate certain plugins

#  Using annotations to drive generation

Using [PojoToRamlClassParserFactory](src/main/java/org/raml/pojotoraml/plugins/PojoToRamlClassParserFactory.java) and 
[PojoToRamlExtensionFactory](src/main/java/org/raml/pojotoraml/plugins/PojoToRamlExtensionFactory.java) as factories will allow you to use
per-class and per-package annotations to control how the RAML is generated for each POJO class.

Some examples:
```java
@RamlGenerator(
        parser = BeanLikeClassParser.class,
        plugins = {@RamlGeneratorPlugin(plugin = "core.changeTypeName", parameters = {"MyValue"})})
public interface HierarchyValue extends TopValue, AnotherTopValue {
  UUID getUUID();
  String getName();
  int getId();
  SubType getSubType();
  List<String> getNames();
}
```
This will activate the core.changeTypeName plugin on this type.  This plugin take one parameter (the type name).  So the type name will not be
HierarchyValue, but MyValue.  Furthermore, it will parse the class with the specified parser (the BeanLikeClassParser.class)

Using that factory, you can also modify the generation of types for which you do not have access to the source code (such a UUID, or InputStream).  
You simply annotate the top package in your project by creating a package-info.java class.
```java
@RamlGenerators({
    @RamlGeneratorForClass(
        forClass = UUID.class,
        generator = @RamlGenerator(parser = BeanLikeClassParser.class,
            plugins = {@RamlGeneratorPlugin(plugin = "core.changeTypeName", parameters = {"string"})})
    )
})
package org.raml.jaxrs.examples.resources;

import org.raml.pojotoraml.plugins.RamlGenerator;
import org.raml.pojotoraml.plugins.RamlGeneratorForClass;
import org.raml.pojotoraml.plugins.RamlGeneratorPlugin;
import org.raml.pojotoraml.plugins.RamlGenerators;
import org.raml.jaxrs.handlers.BeanLikeClassParser;

import java.util.UUID;
``` 

In this example, the UUID class is parsed by the NullClassParser class and it's type is changed to the basic "string" type.
