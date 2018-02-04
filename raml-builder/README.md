# raml-builder

The simplest way to use this code is
```java
public class Main {

    public static void main(String[] args) throws Exception {

        Api api = document()
                .baseUri("http:fun.com/fun")
                .title("Hello!")
                .version("1.0beta6")
                .withTypes(
                        TypeDeclarationBuilder.typeDeclaration("EnumFoo").ofType(TypeBuilder.type().enumValues("UN", "DEUX")),
                        TypeDeclarationBuilder.typeDeclaration("EnumNum").ofType(TypeBuilder.type("integer").enumValues(1,2)),

                        TypeDeclarationBuilder.typeDeclaration("Foo").ofType(
                                TypeBuilder.type("object")
                                        .withFacets(FacetBuilder.facet("required").ofType("boolean"))
                                        .withAnnotations(AnnotationBuilder.annotation("Foo")
                                                .withProperties(
                                                        PropertyValueBuilder.property("time", "2022-02-02"), 
                                                        PropertyValueBuilder.propertyOfArray("count", 1,2)))
                        ),
                        TypeDeclarationBuilder.typeDeclaration("Goo").ofType(TypeBuilder.type("object"))
                )
                .withAnnotationTypes(
                        AnnotationTypeBuilder.annotationType("Foo").withProperty(property("time", "date-only")).withProperty(property("count", "integer[]"))
                )
                .withResources(
                        resource("/no")
                                .description("somedescription")
                                .displayName("somedisplayname")
                                .with(
                                        method("get")
                                                .withQueryParameter(ParameterBuilder.parameter("apaaa").ofType("integer"))
                                                .withAnnotations(
                                                        AnnotationBuilder.annotation("Foo").withProperties(
                                                            PropertyValueBuilder.property("time", "2022-02-02"),
                                                            PropertyValueBuilder.propertyOfArray("count", 7)))
                                                .withBodies(
                                                        BodyBuilder.body("application/json")
                                                                .ofType(TypeBuilder.type("Foo", "Goo")
                                                                        .withProperty(TypePropertyBuilder.property("foo", "string"))
                                                                )
                                                ).withResponses(response(200))
                                )
                ).buildModel();
        }
}
```

At runtime, calling the buildModel() method will throw an exception if you tried to do something illegal.  

Right now, this covers what I need for the jaxrs-to-raml project.  Any extension would be appreciated. :-)
