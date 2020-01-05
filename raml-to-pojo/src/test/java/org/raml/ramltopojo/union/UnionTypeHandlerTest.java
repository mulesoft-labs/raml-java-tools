package org.raml.ramltopojo.union;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.ClassName;
import org.junit.Test;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
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

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-type.raml"), ".");
        UnionTypeHandler handler = new UnionTypeHandler("foo", findTypes("foo", api.types()));

        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.NULL, api,TypeFetchers.fromTypes(), "bar.pack", Collections.<String>emptyList());
        generationContext.newExpectedType("foo",new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl")));
        CreationResult r = handler.create(generationContext,new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertThat(r.getInterface(), is(allOf(name(equalTo("Foo")), methods(containsInAnyOrder(
            allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.UnionType")))),
            allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
            allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("bar.pack", "First")))),
            allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
            allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("bar.pack", "Second"))))
        )))));

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getImplementation().get(), is(allOf(name(equalTo("FooImpl")),
            fields(containsInAnyOrder(
                allOf(fieldName(equalTo("unionType")), fieldType(equalTo(ClassName.get("bar.pack", "Foo.UnionType")))),
                allOf(fieldName(equalTo("firstValue")), fieldType(equalTo(ClassName.get("bar.pack", "First")))),
                allOf(fieldName(equalTo("secondValue")), fieldType(equalTo(ClassName.get("bar.pack", "Second"))))
            )),
            methods(containsInAnyOrder(allOf(methodName(equalTo("<init>"))),
                allOf(methodName(equalTo("<init>")),parameters(contains(type(equalTo(ClassName.get(Object.class)))))),
                allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.UnionType"))), codeContent(equalTo("return this.unionType;\n"))),
                allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox())),codeContent(equalTo("return this.unionType == bar.pack.Foo.UnionType.FIRST;\n"))),
                allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("bar.pack", "First"))),codeContent(equalTo("if (!isFirst()) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: bar.pack.First\");\nreturn this.firstValue;\n"))),
                allOf(methodName(equalTo("isSecond")),returnType(equalTo(ClassName.get(Boolean.class).unbox())),codeContent(equalTo("return this.unionType == bar.pack.Foo.UnionType.SECOND;\n"))),
                allOf(methodName(equalTo("getSecond")),returnType(equalTo(ClassName.get("bar.pack", "Second"))),codeContent(equalTo("if (!isSecond()) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: bar.pack.Second\");\nreturn this.secondValue;\n")))
            )),
            superInterfaces(contains(allOf(typeName(equalTo(ClassName.get("bar.pack", "Foo"))))))
        )));
    }

    @Test
    public void primitiveUnion() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-primitive-type.raml"), ".");
        UnionTypeHandler handler = new UnionTypeHandler("foo", findTypes("foo", api.types()));

        CreationResult r = handler.create(new GenerationContextImpl(PluginManager.NULL, api, TypeFetchers.fromTypes(), "bar.pack",Collections.<String>emptyList()),new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertThat(r.getInterface(), is(allOf(name(equalTo("Foo")), methods(containsInAnyOrder(
            allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.UnionType")))),
            allOf(methodName(equalTo("isInteger")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
            allOf(methodName(equalTo("getInteger")), returnType(equalTo(ClassName.get(Integer.class)))),
            allOf(methodName(equalTo("isSecond")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
            allOf(methodName(equalTo("getSecond")), returnType(equalTo(ClassName.get("", "bar.pack.Second"))))
        )))));

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getImplementation().get(), is(allOf(name(equalTo("FooImpl")),
            fields(containsInAnyOrder(
                allOf(fieldName(equalTo("unionType")), fieldType(equalTo(ClassName.get("bar.pack", "Foo.UnionType")))),
                allOf(fieldName(equalTo("integerValue")), fieldType(equalTo(ClassName.get(Integer.class)))),
                allOf(fieldName(equalTo("secondValue")), fieldType(equalTo(ClassName.get("bar.pack", "Second"))))
            )),
            methods(containsInAnyOrder(methodName(equalTo("<init>")),
                allOf(methodName(equalTo("<init>")),parameters(contains(type(equalTo(ClassName.get(Object.class)))))),
                allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.UnionType"))), codeContent(equalTo("return this.unionType;\n"))),
                allOf(methodName(equalTo("isInteger")),returnType(equalTo(ClassName.get(Boolean.class).unbox())),codeContent(equalTo("return this.unionType == bar.pack.Foo.UnionType.INTEGER;\n"))),
                allOf(methodName(equalTo("getInteger")), returnType(equalTo(ClassName.get(Integer.class))),codeContent(equalTo("if (!isInteger()) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: java.lang.Integer\");\nreturn this.integerValue;\n"))),
                allOf(methodName(equalTo("isSecond")),returnType(equalTo(ClassName.get(Boolean.class).unbox())),codeContent(equalTo("return this.unionType == bar.pack.Foo.UnionType.SECOND;\n"))),
                allOf(methodName(equalTo("getSecond")),returnType(equalTo(ClassName.get("bar.pack", "Second"))),codeContent(equalTo("if (!isSecond()) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: bar.pack.Second\");\nreturn this.secondValue;\n")))
            )),
            superInterfaces(contains(allOf(typeName(equalTo(ClassName.get("bar.pack", "Foo"))))))
        )));
    }

    // @Test(expected = GenerationException.class)
    public void arrayUnion() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-array-type.raml"), ".");
        UnionTypeHandler handler = new UnionTypeHandler("foo", findTypes("foo", api.types()));

        handler.create(new GenerationContextImpl(PluginManager.NULL, api, TypeFetchers.fromTypes(), "bar.pack",Collections.<String>emptyList()), null);
    }

    @Test
    public void nilUnion() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-nil-type.raml"), ".");
        UnionTypeHandler handler = new UnionTypeHandler("foo", findTypes("foo", api.types()));

        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.NULL, api,TypeFetchers.fromTypes(), "bar.pack", Collections.<String>emptyList());
        CreationResult r = handler.create(generationContext, new CreationResult(generationContext.defaultPackage(),ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertThat(r.getInterface(), is(allOf(name(equalTo("Foo")), methods(containsInAnyOrder(
            allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.UnionType")))),
            allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
            allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("bar.pack", "First")))),
            allOf(methodName(equalTo("isNil")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
            allOf(methodName(equalTo("getNil")), returnType(equalTo(ClassName.get("", "java.lang.Object"))))
        )))));

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getImplementation().get(), is(allOf(name(equalTo("FooImpl")),
            fields(containsInAnyOrder(
                allOf(fieldName(equalTo("unionType")), fieldType(equalTo(ClassName.get("bar.pack", "Foo.UnionType")))),
                allOf(fieldName(equalTo("firstValue")), fieldType(equalTo(ClassName.get("bar.pack", "First"))))
            )),
            methods(containsInAnyOrder(allOf(methodName(equalTo("<init>"))),
                allOf(methodName(equalTo("<init>")),parameters(contains(type(equalTo(ClassName.get(Object.class)))))),
                allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.UnionType"))), codeContent(equalTo("return this.unionType;\n"))),
                allOf(methodName(equalTo("isFirst")), returnType(equalTo(ClassName.get(Boolean.class).unbox())),codeContent(equalTo("return this.unionType == bar.pack.Foo.UnionType.FIRST;\n"))),
                allOf(methodName(equalTo("getFirst")), returnType(equalTo(ClassName.get("bar.pack", "First"))),codeContent(equalTo("if (!isFirst()) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: bar.pack.First\");\nreturn this.firstValue;\n"))),
                allOf(methodName(equalTo("isNil")), returnType(equalTo(ClassName.get(Boolean.class).unbox())),codeContent(equalTo("return this.unionType == bar.pack.Foo.UnionType.NIL;\n"))),
                allOf(methodName(equalTo("getNil")), returnType(equalTo(ClassName.get("", "java.lang.Object"))),codeContent(equalTo("if (!isNil()) throw new java.lang.IllegalStateException(\"fetching wrong type out of the union: NullType should be null\");\nreturn null;\n")))
            )),
            superInterfaces(contains(allOf(typeName(equalTo(ClassName.get("bar.pack", "Foo"))))))
        )));
    }
    
    @Test
    public void datesUnion() throws Exception {
        Api api = RamlLoader.load(this.getClass().getResourceAsStream("union-dates.raml"), ".");
        RamlToPojo ramlToPojo = new RamlToPojoBuilder(api).fetchTypes(TypeFetchers.fromAnywhere()).findTypes(TypeFinders.everyWhere()).build(Arrays.asList("core.jackson2"));
        ramlToPojo.buildPojos().creationResults().stream().forEach(x -> {
            System.err.println(x.getInterface().toString());
            System.err.println(x.getImplementation().toString());
        });
    }

    private static UnionTypeDeclaration findTypes(final String name, List<TypeDeclaration> types) {
        return (UnionTypeDeclaration) FluentIterable.from(types).firstMatch(new Predicate<TypeDeclaration>() {

            @Override
            public boolean apply(@Nullable TypeDeclaration input) {
                return input.name().equals(name);
            }

        }).get();
    }
}
