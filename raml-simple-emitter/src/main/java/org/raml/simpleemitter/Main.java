package org.raml.simpleemitter;


import org.raml.builder.*;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;

import java.io.StringReader;
import java.io.StringWriter;

import static org.raml.builder.MethodBuilder.method;
import static org.raml.builder.NodeBuilders.property;
import static org.raml.builder.RamlDocumentBuilder.document;
import static org.raml.builder.ResourceBuilder.resource;
import static org.raml.builder.ResponseBuilder.response;

/**
 * Created by jpbelang on 2017-06-25.
 */
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
                                                .withProperties(PropertyValueBuilder.property("time", "2022-02-02"), PropertyValueBuilder.propertyOfArray("count", 1,2)))
                        ),
                        TypeDeclarationBuilder.typeDeclaration("Goo").ofType(TypeBuilder.type("object"))
                )
                .withAnnotationTypes(
                        AnnotationTypeBuilder.annotationType("Foo").withProperty(property("time", "date-only")).withProperty(property("count", "integer[]"))
                )
                .withResources(
                        resource("/no")
                                .description("fooo!!!")
                                .displayName("Mama!!!")
                                .with(
                                        method("get")
                                                .description("fooofooofooo")
                                                .withQueryParameter(ParameterBuilder.parameter("apaaa").ofType("integer"))
                                                .withAnnotations(AnnotationBuilder.annotation("Foo").withProperties(
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

        StringTypeDeclaration stdzero = (StringTypeDeclaration) api.types().get(0);
        System.err.println(stdzero.enumValues());

        System.err.println(api.types().get(0).name());
        System.err.println();
        Emitter emitter = new Emitter();
        emitter.emit(api);

        StringWriter writer = new StringWriter();
        emitter.emit(api, writer);

        RamlModelResult re_read = new RamlModelBuilder().buildApi(new StringReader(writer.toString()), ".");
        if (re_read.hasErrors()) {
            for (ValidationResult validationResult : re_read.getValidationResults()) {
                System.err.println(validationResult);
            }
        }

        StringTypeDeclaration std = (StringTypeDeclaration) re_read.getApiV10().types().get(0);
        System.err.println(std.enumValues());
    }
}
