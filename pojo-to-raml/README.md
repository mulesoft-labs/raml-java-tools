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

