package org.raml.ramltopojo.object;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.squareup.javapoet.*;
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
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import static org.raml.ramltopojo.RamlLoader.findTypes;
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

    public void mommy(MethodSpec cp) {

    }
    @Test
    public void simplest() throws Exception {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("simplest-type.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

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

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("simplest-containing-simple-array.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

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
    public void usingComposedTypes() throws Exception {

        final Api api = RamlLoader.load(this.getClass().getResourceAsStream("using-composed-type.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

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

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inherited-type.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());

        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getAdditionalProperties")), returnType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class)))),
                        allOf(methodName(equalTo("setAdditionalProperties")), parameters(contains(type(equalTo(TypeName.get(String.class))), type(equalTo(TypeName.get(Object.class)))))),
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
                        allOf(fieldName(equalTo("age")), fieldType(equalTo(ClassName.INT))),
                        allOf(fieldName(equalTo("additionalProperties")), fieldType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class))))
                )),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getAdditionalProperties")), returnType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class)))),
                        allOf(methodName(equalTo("setAdditionalProperties")), parameters(contains(type(equalTo(TypeName.get(String.class))), type(equalTo(TypeName.get(Object.class)))))),
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

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inheritance-with-discriminator-type.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        System.err.println(r.getInterface().toString());
        System.err.println(r.getImplementation().toString());


        assertThat(r.getInterface(), is(allOf(
                name(equalTo("Foo")),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getAdditionalProperties")), returnType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class)))),
                        allOf(methodName(equalTo("setAdditionalProperties")), parameters(contains(type(equalTo(TypeName.get(String.class))), type(equalTo(TypeName.get(Object.class)))))),
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
                        allOf(fieldName(equalTo("name")), fieldType(equalTo(ClassName.get(String.class)))),
                        allOf(fieldName(equalTo("additionalProperties")), fieldType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class))))
                )),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getAdditionalProperties")), returnType(equalTo(ParameterizedTypeName.get(Map.class, String.class, Object.class)))),
                        allOf(methodName(equalTo("setAdditionalProperties")), parameters(contains(type(equalTo(TypeName.get(String.class))), type(equalTo(TypeName.get(Object.class)))))),
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

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inheritance-with-discriminatorvalue-type.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

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

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("multiple-inheritance-type.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

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

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-type.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findTypes("foo", api.types()));

        GenerationContextImpl generationContext = new GenerationContextImpl(api);

        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertThat(r.getInternalTypeForProperty("inside").getInterface(), name(equalTo("InsideType")));
        assertThat(r.getInternalTypeForProperty("inside").getImplementation().get(), name(equalTo("InsideTypeImpl")));
    }

    @Test
    public void pluginCalled() throws Exception {

        final ObjectTypeHandlerPlugin mockPlugin = mock(ObjectTypeHandlerPlugin.class);
        when(mockPlugin.classCreated(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(ObjectTypeDeclaration.class), ArgumentMatchers.any(TypeSpec.Builder.class), eq(EventType.INTERFACE))).thenAnswer(new Answer<TypeSpec.Builder>() {
            @Override
            public TypeSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return (TypeSpec.Builder) invocation.getArguments()[2];
            }
        });
        when(mockPlugin.getterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(ObjectTypeDeclaration.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE))).thenAnswer(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return (MethodSpec.Builder) invocation.getArguments()[2];
            }
        });
        when(mockPlugin.setterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(ObjectTypeDeclaration.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE))).thenAnswer(new Answer<MethodSpec.Builder>() {
            @Override
            public MethodSpec.Builder answer(InvocationOnMock invocation) throws Throwable {
                return (MethodSpec.Builder) invocation.getArguments()[2];
            }
        });


        Api api = RamlLoader.load(this.getClass().getResourceAsStream("plugin-test.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findTypes("foo", api.types()));

        GenerationContextImpl generationContext = new GenerationContextImpl(api) {
            @Override
            public ObjectTypeHandlerPlugin pluginsForObjects(TypeDeclaration... typeDeclarations) {
                return mockPlugin;
            }
        };

        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);
        assertFalse(r.getImplementation().isPresent());
        verify(mockPlugin, times(1)).classCreated(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(ObjectTypeDeclaration.class), ArgumentMatchers.any(TypeSpec.Builder.class), eq(EventType.INTERFACE));
        verify(mockPlugin, times(2)).getterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(StringTypeDeclaration.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE));
        verify(mockPlugin, times(2)).setterBuilt(ArgumentMatchers.any(ObjectPluginContext.class), ArgumentMatchers.any(StringTypeDeclaration.class), ArgumentMatchers.any(MethodSpec.Builder.class), eq(EventType.INTERFACE));
    }

    @Test
    public void checkAnnotations() throws Exception {

        URL url = this.getClass().getResource("plugin-invocation.raml");
        Api api = RamlLoader.load(url.openStream(), new File(url.getFile()).getAbsolutePath());
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", findTypes("foo", api.types()));

        GenerationContextImpl generationContext = new GenerationContextImpl(PluginManager.createPluginManager("org/raml/ramltopojo/object/simple-plugin.properties"), api, TypeFetchers.NULL_FETCHER, "bar.pack", Collections.<String>emptyList());

        CreationResult r = handler.create(generationContext, new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);
        assertTrue(r.getInterface().annotations.size() == 1);
        assertEquals("@java.lang.Deprecated", r.getInterface().annotations.get(0).toString());
        assertTrue(r.getImplementation().get().annotations.size() == 1);
        assertEquals("@java.lang.Deprecated", r.getImplementation().get().annotations.get(0).toString());
    }

    @Test
    public void enumerationsInline() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-enumeration.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

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
    public void unionsInline() {

        Api api = RamlLoader.load(this.getClass().getResourceAsStream("inline-union.raml"), ".");
        ObjectTypeHandler handler = new ObjectTypeHandler("foo", RamlLoader.findTypes("foo", api.types()));

        CreationResult r = handler.create(createGenerationContext(api), new CreationResult("bar.pack", ClassName.get("bar.pack", "Foo"), ClassName.get("bar.pack", "FooImpl"))).get();

        assertNotNull(r);
        assertThat(r.internalType("unionOfPrimitives").getInterface(), is(allOf(

                name(
                        is(equalTo("StringIntegerUnion"))
                ),
                methods(containsInAnyOrder(
                        allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.StringIntegerUnion.UnionType")))),
                        allOf(methodName(equalTo("isString")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getString")), returnType(equalTo(ClassName.get(String.class)))),
                        allOf(methodName(equalTo("isInteger")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getInteger")), returnType(equalTo(ClassName.get(Integer.class))))
                ))

        )));

        assertThat(r.internalType("unionOfOthers").getInterface(), is(allOf(

                name(
                        is(equalTo("OneTwoUnion"))
                ),
                methods(contains(
                        allOf(methodName(equalTo("getUnionType")), returnType(equalTo(ClassName.get("bar.pack", "Foo.OneTwoUnion.UnionType")))),
                        allOf(methodName(equalTo("isOne")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getOne")), returnType(equalTo(ClassName.get("pojo.pack", "One")))),
                        allOf(methodName(equalTo("isTwo")), returnType(equalTo(ClassName.get(Boolean.class).unbox()))),
                        allOf(methodName(equalTo("getTwo")), returnType(equalTo(ClassName.get("pojo.pack", "Two"))))
                ))

        )));

        System.err.println(r.internalType("unionOfOthers").getInterface());
    }
    protected GenerationContextImpl createGenerationContext(final Api api) {
        return new GenerationContextImpl(PluginManager.NULL, api, new TypeFetcher() {
            @Override
            public TypeDeclaration fetchType(Api api, final String name) throws GenerationException {
                return FluentIterable.from(api.types()).firstMatch(new Predicate<TypeDeclaration>() {
                    @Override
                    public boolean apply(@Nullable TypeDeclaration input) {
                        return input.name().equals(name);
                    }
                }).or(new Supplier<TypeDeclaration>() {
                    @Override
                    public TypeDeclaration get() {
                        throw new GenerationException("type " + name + " not found");
                    }
                });
            }
        }, "pojo.pack", Collections.<String>emptyList());
    }


}