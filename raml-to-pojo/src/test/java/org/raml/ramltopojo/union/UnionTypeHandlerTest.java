package org.raml.ramltopojo.union;

import amf.client.model.domain.Shape;
import amf.client.model.domain.UnionShape;
import com.squareup.javapoet.ClassName;
import org.junit.Test;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.GenerationContextImpl;
import org.raml.ramltopojo.RamlLoader;
import org.raml.ramltopojo.TypeFetchers;
import org.raml.ramltopojo.plugin.PluginManager;
import webapi.WebApiDocument;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldName;
import static org.raml.testutils.matchers.FieldSpecMatchers.fieldType;
import static org.raml.testutils.matchers.MethodSpecMatchers.*;
import static org.raml.testutils.matchers.ParameterSpecMatchers.type;
import static org.raml.testutils.matchers.TypeNameMatcher.typeName;
import static org.raml.testutils.matchers.TypeSpecMatchers.*;

/**
 * Created. There, you have it.
 */
public class UnionTypeHandlerTest {
    @Test
    public void simpleUnion() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("union-type.raml"));
        UnionTypeHandler handler = new UnionTypeHandler("foo", findTypes("foo", getDeclaredTypes(api)));

        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.NULL, api, TypeFetchers.fromTypes(), "bar.pack", Collections.<String>emptyList());
        generationContext.newExpectedType("foo", new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl")));
        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();


        assertNotNull(r);

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(contains(
                        allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("bar.pack", "First")))),
                        allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("bar.pack", "Second")))),
                        allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox())))
                ))
        )));

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(contains(
                        allOf(fieldName(equalTo("anyType")), fieldType(equalTo(ClassName.get(Object.class))))
                )),
                methods(contains(
                        allOf(methodName(equalTo("<init>"))),
                        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get("bar.pack", "First")))))),
                        allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("bar.pack", "First"))), codeContent(equalTo(
                                "if ( !(anyType instanceof  bar.pack.First)) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: bar.pack.First\");\nreturn (bar.pack.First) anyType;\n"))),
                        allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox())), codeContent(equalTo("return anyType instanceof bar.pack.First;\n"))),
                        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get("bar.pack", "Second")))))),
                        allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("bar.pack", "Second"))), codeContent(equalTo(
                                "if ( !(anyType instanceof  bar.pack.Second)) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: bar.pack.Second\");\nreturn (bar.pack.Second) anyType;\n"))),
                        allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox())), codeContent(equalTo("return anyType instanceof bar.pack.Second;\n")))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("bar.pack", "Foo"))))
                ))
        )));
    }


    @Test
    public void primitiveUnion() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("union-primitive-type.raml"));
        UnionTypeHandler handler = new UnionTypeHandler("foo", findTypes("foo", getDeclaredTypes(api)));

        CreationResult r = handler.create(new GenerationContextImpl(PluginManager.NULL, api, TypeFetchers.fromTypes(), "bar.pack", Collections.emptyList()), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(contains(
                        allOf(methodName(equalTo("getInteger")), returnType(equalTo(ClassName.get(Integer.class)))),
                        allOf(methodName(equalTo("isInteger")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("", "bar.pack.Second")))),
                        allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox())))
                ))
        )));


        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());


        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(contains(
                        allOf(fieldName(equalTo("anyType")), fieldType(equalTo(ClassName.get(Object.class))))
                )),

                methods(contains(
                        methodName(equalTo("<init>")),
                        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get(Integer.class)))))),
                        allOf(methodName(equalTo("getInteger")), returnType(equalTo(ClassName.get(Integer.class))), codeContent(equalTo(
                                "if ( !(anyType instanceof  java.lang.Integer)) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: java.lang.Integer\");\nreturn (java.lang.Integer) anyType;\n"))),
                        allOf(methodName(equalTo("isInteger")), returnType(equalTo(ClassName.get(Boolean.class).unbox())), codeContent(equalTo("return anyType instanceof java.lang.Integer;\n"))),
                        allOf(methodName(equalTo("<init>")), parameters(contains(type(equalTo(ClassName.get("bar.pack", "Second")))))),
                        allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("bar.pack", "Second"))), codeContent(equalTo(
                                "if ( !(anyType instanceof  bar.pack.Second)) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: bar.pack.Second\");\nreturn (bar.pack.Second) anyType;\n"))),
                        allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox())), codeContent(equalTo("return anyType instanceof bar.pack.Second;\n")))
                )),

                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("bar.pack", "Foo"))))
                ))
        )));

    }

    protected List<Shape> getDeclaredTypes(WebApiDocument api) {
        return api.declares().stream().filter(x -> x instanceof Shape).map(x -> (Shape) x).collect(Collectors.toList());
    }


    private static UnionShape findTypes(final String name, List<Shape> types) {
        return types.stream().filter(input -> input.name().is(name)).map(UnionTypeHandlerTest::castAsNecessary).findFirst().get();
    }

    private static UnionShape castAsNecessary(Shape shape) {

        if ( shape instanceof UnionShape ) {

            return (UnionShape) shape;
        } else {

            return (UnionShape) shape.inherits().get(0);
        }


    }

}
