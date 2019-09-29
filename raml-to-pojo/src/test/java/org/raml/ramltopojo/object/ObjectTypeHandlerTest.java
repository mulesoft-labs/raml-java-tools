package org.raml.ramltopojo.object;

import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;
import amf.client.model.domain.UnionShape;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.ramltopojo.extensions.ObjectTypeHandlerPlugin;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.testutils.UnitTest;
import org.raml.testutils.assertj.ListAssert;
import webapi.Raml10;
import webapi.WebApiDocument;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.squareup.javapoet.Assertions.assertThat;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.raml.ramltopojo.RamlLoader.findShape;
import static org.raml.testutils.matchers.FieldSpecMatchers.*;
import static org.raml.testutils.matchers.MethodSpecMatchers.*;
import static org.raml.testutils.matchers.ParameterSpecMatchers.type;
import static org.raml.testutils.matchers.TypeNameMatcher.typeName;
import static org.raml.testutils.matchers.TypeSpecMatchers.*;

/**
 * Created. There, you have it.
 */
public class ObjectTypeHandlerTest extends UnitTest {

    @Mock
    ObjectPluginContext objectPluginContext;

    @Test
    public void simplest() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("simplest-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", (NodeShape)findShape("foo", api.declares()));

        GenerationContextImpl generationContext = new GenerationContextImpl(api);
        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertThat(r.getInterface())
                .hasName("Foo");

        ListAssert.listMatches(r.getInterface().methodSpecs,

                (c) -> assertThat(c)
                        .hasName("getName")
                        .hasReturnType(ClassName.get(String.class)),
                (c) -> assertThat(c)
                        .hasName("setName")
                        .hasReturnType(ClassName.VOID),
                (c) -> assertThat(c)
                        .hasName("getAge")
                        .hasReturnType(ClassName.INT),
                (c) -> assertThat(c)
                        .hasName("setAge")
                        .hasReturnType(ClassName.VOID));


        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(contains(
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getAge")), returnType(equalTo(ClassName.INT))),
                        allOf(methodName(equalTo("setAge")), parameters(contains(type(equalTo(ClassName.INT)))))
                ))
        )));

        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(contains(
                        allOf(fieldName(equalTo("name")), fieldType(equalTo(ClassName.get(String.class)))),
                        allOf(fieldName(equalTo("age")), fieldType(equalTo(ClassName.INT)))
                )),
                methods(contains(
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class))), codeContent(equalTo("return this.name;\n"))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class))))), codeContent(equalTo("this.name = name;\n"))),
                        allOf(methodName(equalTo("getAge")), returnType(equalTo(ClassName.INT))),
                        allOf(methodName(equalTo("setAge")), parameters(contains(type(equalTo(ClassName.INT)))))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("", "bar.pack.Foo"))))
                ))
        )));

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());
    }

    @Test
    public void simplestContainingSimpleArray() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("simplest-containing-simple-array.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", (NodeShape) findShape("foo", api.declares()));

        GenerationContextImpl generationContext = new GenerationContextImpl(api);
        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();
        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(contains(
                        allOf(methodName(equalTo("getNames")), returnType(equalTo(ParameterizedTypeName.get(List.class, String.class)))),
                        allOf(methodName(equalTo("setNames")), parameters(contains(type(equalTo(ParameterizedTypeName.get(List.class, String.class)))))),
                        allOf(methodName(equalTo("getAges")), returnType(equalTo(ParameterizedTypeName.get(List.class, Integer.class)))),
                        allOf(methodName(equalTo("setAges")), parameters(contains(type(equalTo(ParameterizedTypeName.get(List.class, Integer.class))))))
                ))
        )));


        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(contains(
                        allOf(fieldName(equalTo("names")), fieldType(equalTo(ParameterizedTypeName.get(List.class, String.class)))),
                        allOf(fieldName(equalTo("ages")), fieldType(equalTo(ParameterizedTypeName.get(List.class, Integer.class))))
                )),
                methods(contains(
                        allOf(methodName(equalTo("getNames")), returnType(equalTo(ParameterizedTypeName.get(List.class, String.class)))),
                        allOf(methodName(equalTo("setNames")), parameters(contains(type(equalTo(ParameterizedTypeName.get(List.class, String.class)))))),
                        allOf(methodName(equalTo("getAges")), returnType(equalTo(ParameterizedTypeName.get(List.class, Integer.class)))),
                        allOf(methodName(equalTo("setAges")), parameters(contains(type(equalTo(ParameterizedTypeName.get(List.class, Integer.class))))))
                ))
        )));
    }

    @Test
    public void coo() throws Exception {

        WebApiDocument doc = (WebApiDocument) Raml10.parse("#%RAML 1.0\n" +
                "title: Hello World API\n" +
                "version: v1\n" +
                "baseUri: https://api.github.com\n" +
                "types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    foo:\n" +
                "        type: inherited\n" +
                "        properties:\n" +
                "          name: string\n").get();

        System.err.println(((NodeShape)doc.getDeclarationByName("foo").inherits().get(0).linkTarget().get()).properties());
        System.err.println(((NodeShape)doc.getDeclarationByName("inherited")).properties());
    }

    @Test
    public void coo2() throws Exception {

        WebApiDocument doc = (WebApiDocument) Raml10.parse("#%RAML 1.0\n" +
                "title: Hello World API\n" +
                "version: v1\n" +
                "baseUri: https://api.github.com\n" +
                "types:\n" +
                "    inherited:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    foo:\n" +
                "        type: [inherited]\n" +
                "        properties:\n" +
                "          name: string\n").get();

        System.err.println(((NodeShape)doc.getDeclarationByName("foo").inherits().get(0).linkTarget().orElse(doc.getDeclarationByName("foo").inherits().get(0))).properties());
        System.err.println(((NodeShape)doc.getDeclarationByName("inherited")).properties());
    }

    @Test
    public void coo3() throws Exception {

        WebApiDocument doc = (WebApiDocument) Raml10.parse("#%RAML 1.0\n" +
                "title: Hello World API\n" +
                "version: v1\n" +
                "baseUri: https://api.github.com\n" +
                "types:\n" +
                "    once:\n" +
                "      properties:\n" +
                "        right: string\n" +
                "    twice:\n" +
                "      properties:\n" +
                "        left: string\n" +
                "    foo:\n" +
                "        type: [once, twice]\n" +
                "        properties:\n" +
                "          name: string").get();

        System.err.println(((NodeShape)doc.getDeclarationByName("foo").inherits().get(0)).properties());
        System.err.println(((NodeShape)doc.getDeclarationByName("once")).properties());
    }

    @Test
    public void coo4() throws Exception {

        WebApiDocument doc = (WebApiDocument) Raml10.parse("#%RAML 1.0\n" +
                "title: Hello World API\n" +
                "version: v1\n" +
                "baseUri: https://api.github.com\n" +
                "types:\n" +
                "    first:\n" +
                "      properties:\n" +
                "        age: integer\n" +
                "    second:\n" +
                "      properties:\n" +
                "        area: string\n" +
                "    foo:\n" +
                "        type: string|number").get();

        System.err.println(((UnionShape)doc.getDeclarationByName("foo").inherits().get(0)).anyOf());
    }

    @Test
    public void usingComposedTypes() throws Exception {

        final WebApiDocument api = RamlLoader.load(this.getClass().getResource("using-composed-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findShape("foo", api.declares()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(contains(
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get("", "pojo.pack.Composed")))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get("", "pojo.pack.Composed"))))))
                ))
        )));

        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(contains(
                        allOf(fieldName(equalTo("name")), fieldType(equalTo(ClassName.get("", "pojo.pack.Composed"))))
                )),
                methods(contains(
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get("", "pojo.pack.Composed")))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get("", "pojo.pack.Composed")))))))
                ))
        ));
    }

    @Test
    public void simpleInheritance() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inherited-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findShape("foo", api.declares()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getAge")), returnType(equalTo(ClassName.INT))),
                        allOf(methodName(equalTo("setAge")), parameters(contains(type(equalTo(ClassName.INT))))),
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class))))))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("", "pojo.pack.Inherited"))))

                )))));

        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(containsInAnyOrder(
                        allOf(fieldName(equalTo("name")), fieldType(equalTo(ClassName.get(String.class)))),
                        allOf(fieldName(equalTo("age")), fieldType(equalTo(ClassName.INT)))
                )),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getAge")), returnType(equalTo(ClassName.INT))),
                        allOf(methodName(equalTo("setAge")), parameters(contains(type(equalTo(ClassName.INT)))))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("", "bar.pack.Foo"))))
                ))
        )));
    }

    @Test
    public void inheritanceWithDiscriminator() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inheritance-with-discriminator-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findShape("foo", api.declares()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());


        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getKind")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("getRight")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setRight")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class))))))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("", "pojo.pack.Once"))))

                )))));

        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(containsInAnyOrder(
                        allOf(fieldName(equalTo("kind")), fieldType(equalTo(ClassName.get(String.class))), initializer(equalTo("_DISCRIMINATOR_TYPE_NAME"))),
                        allOf(fieldName(equalTo("right")), fieldType(equalTo(ClassName.get(String.class)))),
                        allOf(fieldName(equalTo("name")), fieldType(equalTo(ClassName.get(String.class))))
                )),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getKind")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("getRight")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setRight")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class))))))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("", "bar.pack.Foo"))))
                ))
        )));

    }

    @Test
    public void inheritanceWithDiscriminatorAndValue() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inheritance-with-discriminatorvalue-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findShape("foo", api.declares()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());


        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(containsInAnyOrder(
                        allOf(fieldName(equalTo("kind")), fieldType(equalTo(ClassName.get(String.class))), initializer(equalTo("_DISCRIMINATOR_TYPE_NAME"))),
                        allOf(fieldName(equalTo("right")), fieldType(equalTo(ClassName.get(String.class)))),
                        allOf(fieldName(equalTo("name")), fieldType(equalTo(ClassName.get(String.class))))
                ))
        )));


    }

    @Test
    public void multipleInheritance() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("multiple-inheritance-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findShape("foo", api.declares()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getLeft")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setLeft")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getRight")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setRight")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class))))))
                )),
                superInterfaces(contains(
                        typeName(equalTo(ClassName.get("", "pojo.pack.Once"))),
                        typeName(equalTo(ClassName.get("", "pojo.pack.Twice")))
                        )

                ))));

        assertThat(r.getImplementation().get(), is(allOf(
                name(equalTo("FooImpl")),
                fields(containsInAnyOrder(
                        allOf(fieldName(equalTo("left")), fieldType(equalTo(ClassName.get(String.class)))),
                        allOf(fieldName(equalTo("right")), fieldType(equalTo(ClassName.get(String.class)))),
                        allOf(fieldName(equalTo("name")), fieldType(equalTo(ClassName.get(String.class))))
                )),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getLeft")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setLeft")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getRight")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setRight")), parameters(contains(type(equalTo(ClassName.get(String.class)))))),
                        allOf(methodName(equalTo("getName")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("setName")), parameters(contains(type(equalTo(ClassName.get(String.class))))))
                )),
                superInterfaces(contains(
                        allOf(typeName(equalTo(ClassName.get("", "bar.pack.Foo"))))
                ))
        )));
    }

    @Test
    public void simplestInternal() throws Exception {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-type.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", (NodeShape) findShape("foo", api.declares()));

        GenerationContextImpl generationContext = new GenerationContextImpl(api);

        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertThat(r.getInternalTypeForProperty("inside").getInterface(), name(equalTo("InsideType")));
        assertThat(r.getInternalTypeForProperty("inside").getImplementation().get(), name(equalTo("InsideTypeImpl")));
    }

    @Test
    public void pluginCalled() throws Exception {

        final ObjectTypeHandlerPlugin mockPlugin = mock(ObjectTypeHandlerPlugin.class);
        when(mockPlugin.classCreated(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(NodeShape.class), ArgumentMatchers.any(TypeSpec.Builder.class), eq(EventType.INTERFACE))).thenAnswer(new Answer<TypeSpec.Builder>() {
            @Override
            public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return (TypeSpec.Builder) invocation.getArguments()[2];
            }
        });
        when(mockPlugin.getterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(PropertyShape.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE))).thenAnswer(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return (MethodSpec.Builder) invocation.getArguments()[2];
            }
        });
        when(mockPlugin.setterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(PropertyShape.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE))).thenAnswer(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return (MethodSpec.Builder) invocation.getArguments()[2];
            }
        });


        WebApiDocument api = RamlLoader.load(this.getClass().getResource("plugin-test.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", (NodeShape) findShape("foo", api.declares()));

        GenerationContextImpl generationContext = new GenerationContextImpl(api) {
            @Override
            public ObjectTypeHandlerPlugin pluginsForObjects(Shape... typeDeclarations) {
                return mockPlugin;
            }
        };

        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);
        assertFalse(r.getImplementation().isPresent());
        verify(mockPlugin, times(1)).classCreated(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(NodeShape.class), ArgumentMatchers.any(TypeSpec.Builder.class), eq(EventType.INTERFACE));
        verify(mockPlugin, times(2)).getterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(PropertyShape.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE));
        verify(mockPlugin, times(2)).setterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(PropertyShape.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE));
    }

    @Test
    public void checkAnnotations() throws Exception {

        URL url = this.getClass().getResource("plugin-invocation.raml");
        WebApiDocument api = RamlLoader.load(url);
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", (NodeShape) findShape("foo", api.declares()));

        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.createPluginManager("org/raml/ramltopojo/object/simple-plugin.properties"), api, TypeFetchers.NULL_FETCHER, "bar.pack", Collections.<String>emptyList());

        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);
        assertTrue(r.getInterface().annotations.size() == 1);
        assertEquals("@java.lang.Deprecated", r.getInterface().annotations.get(0).toString());
        assertTrue(r.getImplementation().get().annotations.size() == 1);
        assertEquals("@java.lang.Deprecated", r.getImplementation().get().annotations.get(0).toString());
    }

    @Test
    public void enumerationsInline() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-enumeration.raml") );
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findShape("foo", api.declares()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);
        assertThat(r.internalType("name").getInterface(), is(allOf(

                name(
                        is(equalTo("NameType"))
                )
        )));

        assertThat(r.internalType("int").getInterface(), is(allOf(

                name(
                        is(equalTo("IntType"))
                )
        )));

        assertThat(r.internalType("num").getInterface(), is(allOf(

                name(
                        is(equalTo("NumType"))
                )
        )));

    }

    @Test
    public void unionsInline() throws ExecutionException, InterruptedException {

        WebApiDocument api = RamlLoader.load(this.getClass().getResource("inline-union.raml"));
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findShape("foo", api.declares()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        System.err.println(r.internalType("unionOfPrimitives").getInterface().toString());
        System.err.println(r.internalType("unionOfPrimitives").getImplementation().toString());


        assertThat(r.internalType("unionOfPrimitives").getInterface(), is(allOf(

                name(
                        is(equalTo("UnionOfPrimitivesType"))
                ),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getInteger")), returnType(equalTo(ClassName.get(Integer.class)))),
                        allOf(methodName(equalTo("isInteger")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getString")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("isString")), returnType(equalTo(ClassName.get(Boolean.class).unbox())))
                ))

        )));

        assertThat(r.internalType("unionOfOthers").getInterface(), is(allOf(

                name(
                        is(equalTo("UnionOfOthersType"))
                ),
                methods(contains(
                        allOf(methodName(equalTo("getOne")), returnType(equalTo(ClassName.get("pojo.pack", "One")))),
                        allOf(methodName(equalTo("isOne")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getTwo")), returnType(equalTo(ClassName.get("pojo.pack", "Two")))),
                        allOf(methodName(equalTo("isTwo")), returnType(equalTo(ClassName.get(Boolean.class).unbox())))
                ))

        )));

        System.err.println(r.internalType("unionOfOthers").getInterface());
    }
    protected GenerationContextImpl createGenerationContext(final WebApiDocument api) {
        return new GenerationContextImpl(PluginManager.NULL, api, null, "pojo.pack", Collections.emptyList());
    }


}